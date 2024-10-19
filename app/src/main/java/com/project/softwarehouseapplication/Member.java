package com.project.softwarehouseapplication;
public class Member {
    private String id;  // Add an id field for member identification
    private String name;
    private String profileImageUrl;

    // Default constructor for Firebase
    public Member() {
    }

    // Constructor with id, name, and profileImageUrl
    public Member(String id, String name, String profileImageUrl) {
        this.id = id;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    // Getter for id
    public String getId() {
        return id;
    }

    // Setter for id
    public void setId(String id) {
        this.id = id;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Setter for name
    public void setName(String name) {
        this.name = name;
    }

    // Getter for profileImageUrl
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    // Setter for profileImageUrl
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}

