package com.app.appfrontlocations;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LocationDAO {
    private final MongoCollection<Document> locationCollection;

    public LocationDAO() {
        MongoClient mongoClient = new MongoDBUtil().getMongoClient();
        locationCollection = mongoClient.getDatabase("businessLocations").getCollection("locations");
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

    public Location getLocationById(String id) {
        Document query = new Document("_id", id);
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
        String locationId = updatedLocation.getId();

        if (getLocationById(locationId) == null) {
            throw new RuntimeException("Location with ID " + locationId + " does not exist");
        }

        Document filter = new Document("_id", locationId);
        Document updated = locationToDocument(updatedLocation);
        try {
            locationCollection.updateOne(filter, new Document("$set", updated));
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error updating location " + e.getMessage(), e);
        }
    }

    public boolean deleteLocation(String id) {

        if (getLocationById(id) == null) {
            throw new RuntimeException("Location with ID " + id + " does not exist");
        }

        Bson filter = new Document("_id", id);
        try {
            locationCollection.deleteOne(filter);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting location " + e.getMessage(), e);
        }
    }
}
