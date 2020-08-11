package io.redis.mongodb;



import com.mongodb.ConnectionString;
import com.mongodb.client.*;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;
import com.redislabs.modules.rejson.JReJSON;
import org.bson.Document;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.StreamEntryID;

import java.util.HashMap;
import java.util.Map;

public class MongoToRedisConnector {

    JedisPool jedisPool;
    MongoClient mongoClient;
    MongoDatabase mongoDatabase;


    String businessObjectName = "customers";

    public MongoToRedisConnector() {
        jedisPool = new JedisPool();
        mongoClient = MongoClients.create("mongodb://127.0.0.1:27017/crm");
        mongoDatabase = mongoClient.getDatabase("crm");
    }

    public void syncData(){

        MongoCollection<Document> customers = mongoDatabase.getCollection(businessObjectName);


        MongoCursor<ChangeStreamDocument<Document>> cursor = customers.watch().fullDocument(FullDocument.UPDATE_LOOKUP).iterator();


            // get all events
            while (cursor.hasNext()) {
                JReJSON jRedisJsonClient = new JReJSON(jedisPool);

                // insert/update the full document in Redis JSON

                //jRedisJsonClient.set("customer:");
                ChangeStreamDocument<Document> eventDoc = cursor.next();

                String redisKey = businessObjectName.concat(":").concat( eventDoc.getDocumentKey().getString( "_id" ).getValue()  );

                Document fullDoc = eventDoc.getFullDocument();

                // save JSON document
                jRedisJsonClient.set( redisKey, fullDoc );


                // post on stream
                try (Jedis jedis = jedisPool.getResource()) {



                    Map<String,String> messageBody = new HashMap<>();
                    messageBody.put("DOCUMENT", eventDoc.getFullDocument().toJson());
                    messageBody.put("KEY", eventDoc.getDocumentKey().toJson());
                    messageBody.put("OP", eventDoc.getOperationType().getValue());
                    messageBody.put("NS", eventDoc.getNamespaceDocument().toJson());

                    jedis.xadd( "events:mongodb:".concat(businessObjectName), StreamEntryID.NEW_ENTRY, messageBody );

                }

                System.out.println(eventDoc);


            }

            jedisPool.close();







    }


}
