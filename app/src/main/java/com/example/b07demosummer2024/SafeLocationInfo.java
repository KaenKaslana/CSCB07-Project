package com.example.b07demosummer2024;

public class SafeLocationInfo {
    private String id;
    private String address;
    private String notes;

    // Required empty constructor for Firebase
    public SafeLocationInfo() { }

    public SafeLocationInfo(String address, String notes) {
        this.address = address;
        this.notes = notes;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}