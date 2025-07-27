package com.example.b07demosummer2024;

public class DocumentInfo {
    public String name;
    public String uri;

    // Required empty constructor for Firebase
    public DocumentInfo() {
    }

    public DocumentInfo(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
