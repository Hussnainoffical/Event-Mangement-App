package com.example.newteachverse;

import java.io.Serializable;

public class Event implements Serializable {

    private int id; // for database primary key
    private String name;
    private String date;
    private String type;
    private String description;
    private String location;
    private boolean isJoined;
    private String imageUri;

    public Event() {
        // default constructor
    }

    public Event(int id, String name, String date, String type, String description, String location) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.type = type;
        this.description = description;
        this.location = location;
    }

    public Event(String name, String date, String type, String description, String location) {
        this.name = name;
        this.date = date;
        this.type = type;
        this.description = description;
        this.location = location;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public boolean isJoined() {
        return isJoined;
    }

    public String getImageUri() {
        return imageUri;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setJoined(boolean joined) {
        isJoined = joined;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
