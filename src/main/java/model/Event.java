package model;

import java.sql.Timestamp;

public class Event {
    private int id;
    private String title;
    private String description;
    private String location;
    private Timestamp eventDate;

    // Constructors
    public Event() {}

    public Event(String title, String description, String location, Timestamp eventDate) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.eventDate = eventDate;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Timestamp getEventDate() { return eventDate; }
    public void setEventDate(Timestamp eventDate) { this.eventDate = eventDate; }
}