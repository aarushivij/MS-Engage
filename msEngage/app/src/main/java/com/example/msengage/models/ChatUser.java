package com.example.msengage.models;

public class ChatUser {

    String uid;
    String name;


    public ChatUser() {
    }

    public ChatUser(String uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
