package com.work37.napnap.ui.userlogin_register;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

@Entity
public class User implements Cloneable, Serializable {

    @PrimaryKey
    @NonNull
    private Long uid;
    private String userAccount;
    private String userName;
    public int fanNum;
    public int followNum;

    public String profile;

    private String userAvatar;
    public User() {
    }

    @Ignore
    public User(String userAccount, String password) {
        this.userAccount = userAccount;
    }

    @Ignore
    public User(Long uid,String userAccount,String userAvatar){
        this.uid = uid;
        this.userAccount = userAccount;
        this.userAvatar = userAvatar;
    }

    public User(JSONObject data) {
        try {
            this.uid = data.getLong("id");
            this.userAccount = data.getString("userAccount");
            this.userName = data.getString("username");
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
    public User(Long uid, String userAccount, String userName,int fanNum, int followNum,@Nullable String profile,@Nullable String userAvator) {
        this.uid = uid;
        this.userAccount = userAccount;
        this.userName = userName;
        this.fanNum = fanNum;
        this.followNum = followNum;
        this.profile = profile;
        this.userAvatar=userAvator;
    }

    @Ignore
    public User(Long uid, String userName,int fanNum, int followNum,@Nullable String profile,@Nullable String userAvator) {
        this.uid = uid;
        this.userName = userName;
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

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
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
                ", userAccount='" + userAccount + '\'' +
                ", username='" + userName + '\'' +
                ", fanNum=" + fanNum +
                ", followNum=" + followNum +
                ", profile='" + profile + '\'' +
                ", userAvatar='" + userAvatar + '\'' +
                '}';
    }
}
