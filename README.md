
# Moving data from MongoDB to Redis

This quick & dirty demonstration show how using [MongoDB ChangeStreams](https://docs.mongodb.com/manual/changeStreams/) 
it is possible to capture MongoDB events to:

* [Save Documents in to Redis JSON](https://github.com/tgrall/mongodb-to-redis/blob/77f029c09c8f878e07b190ebc4c96b69bc9eb2f3/connector/src/main/java/io/redis/mongodb/MongoToRedisConnector.java#L55)
* [Send the MongoDB Event to Redis Streams](https://github.com/tgrall/mongodb-to-redis/blob/77f029c09c8f878e07b190ebc4c96b69bc9eb2f3/connector/src/main/java/io/redis/mongodb/MongoToRedisConnector.java#L63-L69)


This first drop is not a producr or a real demonstration but just a starting point for discussion.

You can see it in action in [short video](https://www.youtube.com/watch?v=NgYH12E6gZA).


