package com.app.appfrontlocations;

import com.app.appfrontlocations.exceptions.LocationOperationException;
import com.app.appfrontlocations.exceptions.LocationConversionException;
import com.app.appfrontlocations.exceptions.LocationNotFoundException;
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
    private static final String DB_NAME = "businessLocations";
    private static final String COLLECTION_NAME = "locations";
    private static final String UNIQUE_INDEX = "_name";

    public LocationDAO() {
        MongoClient mongoClient = new MongoDBUtil().getMongoClient();
        locationCollection = mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION_NAME);
        createUniqueIndex();
    }

    private void createUniqueIndex() {
        IndexOptions indexOptions = new IndexOptions().unique(true);
        locationCollection.createIndex(new Document(UNIQUE_INDEX, 1), indexOptions);
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
                } catch (Exception e) {
                    throw new LocationConversionException("Error creating location from document: " + e.getMessage(), e);
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
        } catch (Exception e) {
            throw new LocationConversionException("Error creating document from location: " + e.getMessage(), e);
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
            throw new LocationOperationException("Error inserting location " + e.getMessage(), e);
        }
    }

    public boolean updateLocation(Location updatedLocation) {
        String locationName = updatedLocation.getName();
        if (getLocationByField(UNIQUE_INDEX, locationName) == null) {
            throw new LocationNotFoundException("Location" + locationName + " not found");
        }
        Document filter = new Document(UNIQUE_INDEX, locationName);
        Document updated = locationToDocument(updatedLocation);
        try {
            locationCollection.updateOne(filter, new Document("$set", updated));
            return true;
        } catch (Exception e) {
            throw new LocationOperationException("Error updating location " + e.getMessage(), e);
        }
    }

    public boolean addOrUpdateLocation(Location location) {
        String locationName = location.getName();
        Document filter = new Document(UNIQUE_INDEX, locationName);
        Document updated = locationToDocument(location);
        UpdateOptions options = new UpdateOptions().upsert(true);
        try {
            UpdateResult result = locationCollection.updateOne(filter, new Document("$set", updated), options);
            return result.getModifiedCount() > 0 || result.getUpsertedId() != null;
        } catch (Exception e) {
            throw new LocationOperationException("Error adding or updating location " + e.getMessage(), e);
        }
    }

    public boolean deleteLocation(String name) {
        if (getLocationByField(UNIQUE_INDEX, name) == null) {
            throw new LocationNotFoundException("Location" + name + " not found");
        }
        Bson filter = new Document(UNIQUE_INDEX, name);
        try {
            locationCollection.deleteOne(filter);
            return true;
        } catch (Exception e) {
            throw new LocationOperationException("Error deleting location " + e.getMessage(), e);
        }
    }
}
