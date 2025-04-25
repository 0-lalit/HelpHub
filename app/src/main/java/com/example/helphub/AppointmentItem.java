package com.example.helphub;

public class AppointmentItem {
    private String name;
    private String course;
    private String date;
    private String time;
    private String topic;

    // Required empty constructor
    public AppointmentItem() {}

    public AppointmentItem(String name, String course, String date, String time, String topic) {
        this.name = name;
        this.course = course;
        this.date = date;
        this.time = time;
        this.topic = topic;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
}
