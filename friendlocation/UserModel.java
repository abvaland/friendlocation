package com.example.ajay.friendlocation;

/**
 * Created by Ajay on 13-04-2017.
 */

public class UserModel {
    String userId;
    String name;
    String latitude;
    String longitude;
    String dateTime;

    @Override
    public String toString() {
        return "UserModel{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String getUserId() {
        return userId;
    }

    public UserModel setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getLatitude() {
        return latitude;
    }

    public UserModel setLatitude(String latitude) {
        this.latitude = latitude;
        return this;
    }

    public String getLongitude() {
        return longitude;
    }

    public UserModel setLongitude(String longitude) {
        this.longitude = longitude;
        return this;
    }

    public String getDateTime() {
        return dateTime;
    }

    public UserModel setDateTime(String dateTime) {
        this.dateTime = dateTime;
        return this;
    }
}
