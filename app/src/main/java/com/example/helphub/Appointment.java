package com.example.helphub;

public class Appointment {
    public String id;
    public String studentId;
    public String studentEmail;
    public String gaTaName;
    public String gaTaCourse;
    public String date;
    public String time;
    public String topic;

    public Appointment() {}

    public Appointment(String id, String studentId, String studentEmail, String gaTaName,
                       String gaTaCourse, String date, String time, String topic) {
        this.id = id;
        this.studentId = studentId;
        this.studentEmail = studentEmail;
        this.gaTaName = gaTaName;
        this.gaTaCourse = gaTaCourse;
        this.date = date;
        this.time = time;
        this.topic = topic;
    }
}
