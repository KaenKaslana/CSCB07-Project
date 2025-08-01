package com.example.b07demosummer2024;

public class Item {

    private String id;
    private String title;

    private String description;

    public Item() {}

    public Item(String id, String title,  String description) {
        this.id = id;
        this.title = title;

        this.description = description;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }


    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
