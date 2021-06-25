package com.example.diabeteshealthmonitoringapplication;

public class User {
    private String uid,username,email,phone;

    /***
     * Default empty constructor for firebase
     * with uid @param.uid
     */
    public User() {

    }

    public User(String uid, String username, String email, String phone) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
