package com.app.appfrontlocations;

public class Location {
    private String _id;
    private String _name;
    private String _address;
    private String _status;

    public Location() {
        // No-argument constructor for JSON deserialization
    }

    public Location(String id, String name, String address, String status) {
        if (id == null || name == null || address == null || status == null) {
            throw new IllegalArgumentException("Fields cannot be null");
        }
        _id = id;
        _name = name;
        _address = address;
        _status = status;
    }

    public String getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public String getAddress() {
        return _address;
    }

    public String getStatus() {
        return _status;
    }

    public void setId(String id) {
        _id = id;
    }

    public void setName(String name) {
        _name = name;
    }

    public void setAddress(String address) {
        _address = address;
    }

    public void setStatus(String status) {
        _status = status;
    }
}