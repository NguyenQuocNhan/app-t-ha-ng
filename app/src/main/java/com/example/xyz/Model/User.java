package com.example.xyz.Model;

public class User {
    private String ID;
    private String username;
    private String gender;
    private String number;
    private String actor;
    private String avatar;
    private String address;

    User() {}

    public User(String ID, String username, String gender, String number, String actor, String avatar, String address) {
        this.ID = ID;
        this.username = username;
        this.gender = gender;
        this.number = number;
        this.actor = actor;
        this.avatar = avatar;
        this.address = address;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}