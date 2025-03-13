package com.example.helphub;

public class AppointmentItem {
    private String gaTaName;
    private String course;
    private String date;
    private String time;
    private String topic;

    public AppointmentItem(String gaTaName, String course, String date, String time, String topic) {
        this.gaTaName = gaTaName;
        this.course = course;
        this.date = date;
        this.time = time;
        this.topic = topic;
    }

    public String getGaTaName() {
        return gaTaName;
    }

    public String getCourse() {
        return course;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getTopic() {
        return topic;
    }
} 