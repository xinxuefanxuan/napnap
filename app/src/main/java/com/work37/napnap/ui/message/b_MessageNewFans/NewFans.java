package com.work37.napnap.ui.message.b_MessageNewFans;

import androidx.annotation.NonNull;

import java.util.Date;

public class NewFans {

    private Long id;
    private String userAccount;
    private String userName;
    private String profile;
    private String userAvatar;
    private Date createTime;


    public NewFans(Long id, String userAccount, String userName, String profile, String userAvatar, Date createTime) {
        this.id = id;
        this.userAccount = userAccount;
        this.userName = userName;
        this.profile = profile;
        this.userAvatar = userAvatar;
        this.createTime = createTime;
    }





    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @NonNull
    @Override
    public String toString() {
        return "NewFans{" +
                "id=" + id +
                ", userAccount='" + userAccount + '\'' +
                ", userName='" + userName + '\'' +
                ", profile='" + profile + '\'' +
                ", userAvatar='" + userAvatar + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
