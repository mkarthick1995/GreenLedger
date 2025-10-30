package com.greenledger.app.models;

public class User {
    private String userId;
    private String name;
    private String phone;
    private String userType; // "Farmer" or "Labourer"
    private long createdAt;

    public User() {
        // Required empty constructor for Firebase
    }

    public User(String userId, String name, String phone, String userType) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.userType = userType;
        this.createdAt = System.currentTimeMillis();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
