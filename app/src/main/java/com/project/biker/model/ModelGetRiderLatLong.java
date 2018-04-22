package com.project.biker.model;

/**
 * Created by Vikas Patel on 7/19/2017.
 */

public class ModelGetRiderLatLong {

    private String userId;
    private String riderId;
    private String riderLat;
    private String riderLong;
    private String firstName;
    private String lastName;
    private String userType;
    private String meetUpLatitude;
    private String meetUpLongitude;
    private String destinationLatitude;
    private String destinationLongitude;
    private String isFriend;

    public String getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(String isFriend) {
        this.isFriend = isFriend;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }


    public String getMeetUpLatitude() {
        return meetUpLatitude;
    }

    public void setMeetUpLatitude(String meetUpLatitude) {
        this.meetUpLatitude = meetUpLatitude;
    }

    public String getMeetUpLongitude() {
        return meetUpLongitude;
    }

    public void setMeetUpLongitude(String meetUpLongitude) {
        this.meetUpLongitude = meetUpLongitude;
    }

    public String getDestinationLatitude() {
        return destinationLatitude;
    }

    public void setDestinationLatitude(String destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    public String getDestinationLongitude() {
        return destinationLongitude;
    }

    public void setDestinationLongitude(String destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRiderId() {
        return riderId;
    }

    public void setRiderId(String riderId) {
        this.riderId = riderId;
    }

    public String getRiderLat() {
        return riderLat;
    }

    public void setRiderLat(String riderLat) {
        this.riderLat = riderLat;
    }

    public String getRiderLong() {
        return riderLong;
    }

    public void setRiderLong(String riderLong) {
        this.riderLong = riderLong;
    }
}
