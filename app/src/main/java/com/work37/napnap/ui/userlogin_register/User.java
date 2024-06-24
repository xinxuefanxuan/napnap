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
    private Long id;
    private String userAccount;
    private String userName;
    public Long fansNum;
    public Long focusNum;

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
        this.id = uid;
        this.userAccount = userAccount;
        this.userAvatar = userAvatar;
    }

    @Ignore
    public User(String userName,String userAvatar,Long id){
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.id = id;
    }

    @Ignore
    public User(String userAccount,String userName,String userAvatar){
        this.userAccount = userAccount;
        this.userName = userName;
        this.userAvatar = userAvatar;
    }

    public User(JSONObject data) {
        try {
            this.id = data.getLong("id");
            this.userAccount = data.getString("userAccount");
            this.userName = data.getString("username");
            this.userAvatar = data.getString("avatar");
            this.fansNum = (data.getLong("fansNum"));
            this.focusNum = (data.getLong("focusNum"));
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
    public User(Long uid, String userAccount, String userName,Long fansNum, Long focusNum,@Nullable String profile,@Nullable String userAvator) {
        this.id = uid;
        this.userAccount = userAccount;
        this.userName = userName;
        this.fansNum = fansNum;
        this.focusNum = focusNum;
        this.profile = profile;
        this.userAvatar=userAvator;
    }

    @Ignore
    public User(Long uid, String userName,Long fansNum, Long focusNum,@Nullable String profile,@Nullable String userAvator) {
        this.id = uid;
        this.userName = userName;
        this.fansNum = fansNum;
        this.focusNum = focusNum;
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

    public Long getId() {
        return id;
    }

    public void setId(Long uid) {
        this.id = uid;
    }


    public Long getFanNum() {
        return fansNum;
    }

    public void setFanNum(Long fanNum) {
        this.fansNum = fanNum;
    }

    public Long getFollowNum() {
        return focusNum;
    }

    public void setFollowNum(Long followNum) {
        this.focusNum = followNum;
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
                "uid=" + id +
                ", userAccount='" + userAccount + '\'' +
                ", username='" + userName + '\'' +
                ", fanNum=" + fansNum +
                ", followNum=" + focusNum +
                ", profile='" + profile + '\'' +
                ", userAvatar='" + userAvatar + '\'' +
                '}';
    }
}
