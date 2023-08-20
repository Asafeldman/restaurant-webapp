package com.app.appfrontlocations;

import com.mongodb.MongoClientSettings;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongoDBUtil {
    private static final String DB_URI = "mongodb://localhost:27017";
    private static MongoClient mongoClient;

    public MongoDBUtil() {
        ConnectionString connectionString = new ConnectionString(DB_URI);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        mongoClient = MongoClients.create(settings);
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }
}
