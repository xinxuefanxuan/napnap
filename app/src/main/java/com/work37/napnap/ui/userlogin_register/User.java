package com.work37.napnap.ui.userlogin_register;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

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

    public User(JSONObject data) {
        try {
            this.uid = data.getInt("id");
            this.username = data.getString("username");
            this.userAvatar = data.getString("avatar");
            this.fanNum = (data.getInt("fansNum"));
            this.followNum = (data.getInt("followNum"));
            this.profile = data.getString("profile");

        } catch (JSONException e) {
            Log.d("TEST", "LoginedUser: JSON error");
            e.printStackTrace();
        }
    }

    @Nullable
    public String getUserAvatar() {
        return userAvatar;
    }

    @Nullable
    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    @Ignore
    public User(int uid, String username, int fanNum, int followNum,@Nullable String profile,@Nullable String userAvator) {
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

    @Nullable
    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", username='" + username + '\'' +
                ", fanNum=" + fanNum +
                ", followNum=" + followNum +
                ", profile='" + profile + '\'' +
                ", userAvatar='" + userAvatar + '\'' +
                '}';
    }
}
