package com.example.musiclovers.models;

import java.util.Date;

public class userItem {
    String _id;
    String userName;
    String email;
    String avatar;
    Date create_at;

    public userItem(String userName, String email, String avatar, Date create_at) {
        this.userName = userName;
        this.email = email;
        this.avatar = avatar;
        this.create_at = create_at;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatar() {
        return avatar;
    }

    public Date getCreate_at() {
        return create_at;
    }

    public String get_id(){
        return  _id;
    }
}
