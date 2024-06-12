package com.work37.napnap.ui.userlogin_register;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class User implements Cloneable{

    @PrimaryKey
    @NonNull
    private int uid;
    private String username;
    public int fanNum;
    public int followNum;

    public String profile;

    private String userAvatar;
    public User() {
    }

    @Ignore
    public User(String username, String password) {
        this.username = username;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    @Ignore
    public User(int uid, String username, int fanNum, int followNum, String profile,String userAvator) {
        this.uid = uid;
        this.username = username;
        this.fanNum = fanNum;
        this.followNum = followNum;
        this.profile = profile;
        this.userAvatar=userAvator;
    }

    @NonNull
    @Override
    protected User clone() throws CloneNotSupportedException {
        User user = null;
        try{
            user = (User) super.clone();
        }catch (CloneNotSupportedException e){
            e.printStackTrace();
        }
        return user;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getFanNum() {
        return fanNum;
    }

    public void setFanNum(int fanNum) {
        this.fanNum = fanNum;
    }

    public int getFollowNum() {
        return followNum;
    }

    public void setFollowNum(int followNum) {
        this.followNum = followNum;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
