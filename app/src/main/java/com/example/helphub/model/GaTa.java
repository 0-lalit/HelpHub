package com.example.helphub.model;

public class GaTa {
    private String id;
    private String name;
    private String email;
    private String role; // "GA" or "TA"
    private String department;
    private String course;
    private String officeHours;
    private String officeLocation;

    // Default constructor required for Firestore
    public GaTa() {}

    public GaTa(String name, String email, String role, String department, 
                String course, String officeHours, String officeLocation) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.department = department;
        this.course = course;
        this.officeHours = officeHours;
        this.officeLocation = officeLocation;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getOfficeHours() {
        return officeHours;
    }

    public void setOfficeHours(String officeHours) {
        this.officeHours = officeHours;
    }

    public String getOfficeLocation() {
        return officeLocation;
    }

    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }
} 