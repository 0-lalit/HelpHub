package com.example.helphub;

public class MessageItem {
    private String sender;
    private String content;
    private String time;
    private boolean isFromMe;

    public MessageItem(String sender, String content, String time, boolean isFromMe) {
        this.sender = sender;
        this.content = content;
        this.time = time;
        this.isFromMe = isFromMe;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public boolean isFromMe() {
        return isFromMe;
    }
} 