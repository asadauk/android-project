package com.project.softwarehouseapplication;
public class User {
    private String name;
    private String email;
    // Default constructor required for Firebase deserialization
    public User() {
        // It's a good practice to leave the default constructor empty.
    }
    // Constructor to initialize the User object with name and email
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
    // Public getter methods are required for Firebase to serialize and deserialize the class
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}

