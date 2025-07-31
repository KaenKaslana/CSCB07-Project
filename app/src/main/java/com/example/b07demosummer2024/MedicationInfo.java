package com.example.b07demosummer2024;

public class MedicationInfo {
    private String id;    // Firebase key
    private String name;
    private String dosage;

    // Required empty constructor for Firebase
    public MedicationInfo() {}

    public MedicationInfo(String name, String dosage) {
        this.name = name;
        this.dosage = dosage;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
}

