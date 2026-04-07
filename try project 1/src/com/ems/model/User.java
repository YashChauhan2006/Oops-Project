package com.ems.model;

import java.util.Date;

public class User {
    private int userId;
    private String fullName;
    private String email;
    private String password;
    private Role role;
    private String academicDetails;
    private String profileImagePath;
    private Date createdAt;

    public enum Role {
        STUDENT, TEACHER, ADMIN
    }

    // Default constructor
    public User() {}

    // Parameterized constructor
    public User(int userId, String fullName, String email, String password, Role role) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getAcademicDetails() { return academicDetails; }
    public void setAcademicDetails(String academicDetails) { this.academicDetails = academicDetails; }

    public String getProfileImagePath() { return profileImagePath; }
    public void setProfileImagePath(String profileImagePath) { this.profileImagePath = profileImagePath; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
