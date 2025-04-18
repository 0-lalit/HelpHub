package com.example.helphub;

import android.os.Parcel;
import android.os.Parcelable;

public class GaTaItem implements Parcelable {
    private String id;
    private String name;
    private String course;
    private boolean available;
    private String profileImageUrl;

    // Default constructor required for Firebase
    public GaTaItem() {}

    public GaTaItem(String name, String course, boolean available, String profileImageUrl) {
        this.name = name;
        this.course = course;
        this.available = available;
        this.profileImageUrl = profileImageUrl;
    }

    protected GaTaItem(Parcel in) {
        id = in.readString();
        name = in.readString();
        course = in.readString();
        available = in.readByte() != 0;
        profileImageUrl = in.readString();
    }

    public static final Creator<GaTaItem> CREATOR = new Creator<GaTaItem>() {
        @Override
        public GaTaItem createFromParcel(Parcel in) {
            return new GaTaItem(in);
        }

        @Override
        public GaTaItem[] newArray(int size) {
            return new GaTaItem[size];
        }
    };

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

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(course);
        dest.writeByte((byte) (available ? 1 : 0));
        dest.writeString(profileImageUrl);
    }
} 