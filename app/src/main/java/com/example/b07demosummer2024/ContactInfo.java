package com.example.b07demosummer2024;

public class ContactInfo {

    private String id;

    private String name;
    private String relationship;
    private String phone;
    private String address;

    public ContactInfo() {} // Required by Firebase

    public ContactInfo(String name, String relationship, String phone, String address) {
        this.name = name;
        this.relationship = relationship;
        this.phone = phone;
        this.address = address;
    }

    // --- ID Methods ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    // --- Name Methods ---
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // --- Relationship Methods ---
    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }

    // --- Phone Methods ---
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    // --- Address Methods ---
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}

