package com.app.appfrontlocations;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LocationDAO {
    private final MongoCollection<Document> locationCollection;

    public LocationDAO() {
        MongoClient mongoClient = new MongoDBUtil().getMongoClient();
        String dbName = "businessLocations";
        String collectionName = "locations";
        locationCollection = mongoClient.getDatabase(dbName).getCollection(collectionName);
        IndexOptions indexOptions = new IndexOptions().unique(true);
        locationCollection.createIndex(new Document("_name", 1), indexOptions);
    }

    public Location documentToLocation(Document document) {
        Field[] fields = Location.class.getDeclaredFields();
        Location location = new Location();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            if (document.containsKey(fieldName)) {
                try {
                    field.set(location, document.get(fieldName));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Error setting field value: " + e.getMessage(), e);
                }
            }
        }
        return location;
    }

    public Document locationToDocument(Location location) {
        Document document = new Document();
        try {
            Field[] fields = Location.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                document.append(field.getName(), field.get(location));
            }
            return document;
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error creating document from Location: " + e.getMessage(), e);
        }
    }

    public List<Location> getAllLocations() {
        List<Location> locations = new ArrayList<>();
        FindIterable<Document> documents = locationCollection.find();
        for (Document doc : documents) {
            Location location = documentToLocation(doc);
            locations.add(location);
        }
        return locations;
    }

    public Location getLocationByField(String fieldName, Object value) {
        Document query = new Document(fieldName, value);
        Document result = locationCollection.find(query).first();
        return result != null ? documentToLocation(result) : null;
    }

    public boolean insertLocation(Location location) {
        Document document = locationToDocument(location);
        try {
            locationCollection.insertOne(document);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error inserting location " + e.getMessage(), e);
        }
    }

    public boolean updateLocation(Location updatedLocation) {
        String locationName = updatedLocation.getName();
        if (getLocationByField("_name", locationName) == null) {
            throw new RuntimeException("Location " + locationName + " does not exist");
        }
        Document filter = new Document("_name", locationName);
        Document updated = locationToDocument(updatedLocation);
        try {
            locationCollection.updateOne(filter, new Document("$set", updated));
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error updating location " + e.getMessage(), e);
        }
    }

    public boolean addOrUpdateLocation(Location location) {
        String locationName = location.getName();
        Document filter = new Document("_name", locationName);
        Document updated = locationToDocument(location);
        UpdateOptions options = new UpdateOptions().upsert(true);
        try {
            UpdateResult result = locationCollection.updateOne(filter, new Document("$set", updated), options);
            return result.getModifiedCount() > 0 || result.getUpsertedId() != null; // true if updated or inserted
        } catch (Exception e) {
            throw new RuntimeException("Error adding or updating location " + e.getMessage(), e);
        }
    }


    public boolean deleteLocation(String name) {
        if (getLocationByField("_name", name) == null) {
            throw new RuntimeException("Location name: " + name + " does not exist");
        }
        Bson filter = new Document("_name", name);
        try {
            locationCollection.deleteOne(filter);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting location " + e.getMessage(), e);
        }
    }
}
