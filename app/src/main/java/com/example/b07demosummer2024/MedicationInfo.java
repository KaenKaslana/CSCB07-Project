package com.example.b07demosummer2024;

/**
 * Model class representing a user's medication record stored in Firebase.
 * 
 * Contains an auto-generated Firebase key {@code id}, the medication {@code name},
 * and the {@code dosage} instructions.
 */
public class MedicationInfo {
    /** Firebase-generated unique key for this medication. */
    private String id;

    /** Name of the medication. */
    private String name;

    /** Dosage instructions for the medication. */
    private String dosage;

    /**
     * Required empty constructor for Firebase deserialization.
     */
    public MedicationInfo() {}

    /**
     * Constructs a MedicationInfo with the given name and dosage.
     *
     * @param name   Name of the medication.
     * @param dosage Dosage instructions for the medication.
     */
    public MedicationInfo(String name, String dosage) {
        this.name = name;
        this.dosage = dosage;
    }

    /**
     * Returns the Firebase key of this medication.
     *
     * @return Firebase-generated ID string.
     */
    public String getId() { return id; }

    /**
     * Sets the Firebase key for this medication.
     *
     * @param id Firebase-generated ID string.
     */
    public void setId(String id) { this.id = id; }

    /**
     * Returns the name of the medication.
     *
     * @return Medication name.
     */
    public String getName() { return name; }

    /**
     * Sets the name of the medication.
     *
     * @param name Medication name.
     */
    public void setName(String name) { this.name = name; }

    /**
     * Returns the dosage instructions.
     *
     * @return Medication dosage instructions.
     */
    public String getDosage() { return dosage; }

    /**
     * Sets the dosage instructions for the medication.
     *
     * @param dosage Medication dosage instructions.
     */
    public void setDosage(String dosage) { this.dosage = dosage; }
}

