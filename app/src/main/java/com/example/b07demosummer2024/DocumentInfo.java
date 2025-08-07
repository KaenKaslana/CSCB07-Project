package com.example.b07demosummer2024;

/**
 * Model class representing a document's metadata stored in Firebase.
 * 
 * Contains a document name and its URI. Includes an empty constructor
 * required by Firebase for data mapping.
 */
public class DocumentInfo {
    /** The display name of the document. */
    public String name;
    /** The URI string pointing to the document's location. */
    public String uri;

    /**
     * Required public no-argument constructor for Firebase deserialization.
     */
    public DocumentInfo() {
    }

    /**
     * Constructs a new DocumentInfo with the given name and URI.
     *
     * @param name the display name of the document
     * @param uri  the URI string where the document is stored
     */
    public DocumentInfo(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    /**
     * Returns the document's display name.
     *
     * @return the name of the document
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a new display name for the document.
     *
     * @param name the new name to assign
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the URI string of the document's location.
     *
     * @return the document's URI
     */
    public String getUri() {
        return uri;
    }

    /**
     * Sets a new URI string for the document's location.
     *
     * @param uri the URI to assign
     */
    public void setUri(String uri) {
        this.uri = uri;
    }
}
