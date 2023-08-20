package com.app.appfrontlocations;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class LocationDAO {
    private final MongoCollection<Document> locationCollection;

    public LocationDAO() {
        MongoClient mongoClient = new MongoDBUtil().getMongoClient();
        locationCollection = mongoClient.getDatabase("businessLocations").getCollection("locations");
    }

    public Location documentToLocation(Document document) {
        String id = document.getString("_id");
        String name = document.getString("_name");
        String address = document.getString("_address");
        String status = document.getString("_status");
        return new Location(id, name, address, status);
    }

    public Document locationToDocument(Location location) {
        Document document = new Document();
        document.append("_id", location.getId());
        document.append("_name", location.getName());
        document.append("_address", location.getAddress());
        document.append("_status", location.getStatus());
        return document;
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

    public boolean updateLocation(String locationId, String newName, String newAddress, String newStatus) {
        Document filter = new Document("_id", locationId);
        Document updated = new Document();
        if (newName != null) {
            updated.append("_name", newName);
        }
        if (newAddress != null) {
            updated.append("_address", newAddress);
        }
        if (newStatus != null && (newStatus.equals("Open") || newStatus.equals("Closed"))) {
            updated.append("_status", newStatus);
        }
        try {
            locationCollection.updateOne(filter, new Document("$set", updated));
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error updating location " + e.getMessage(), e);
        }
    }

    public boolean deleteLocation(String id) {
        Bson filter = new Document("_id", id);
        try {
            locationCollection.deleteOne(filter);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting location " + e.getMessage(), e);
        }
    }


}
