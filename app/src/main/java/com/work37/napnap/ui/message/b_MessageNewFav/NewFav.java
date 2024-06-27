package com.work37.napnap.ui.message.b_MessageNewFav;

import java.util.Date;

public class NewFav {
    private Long id;
    private String userName;
    private String userAccount;
    private String userAvatar;
    private Long fansNum;
    private Long focusNum;
    private String userProfile;
    private Long postId;
    private String postTitle;
    private Date createTime;

    public NewFav(Long id, String userName, String userAccount, String userAvatar, Long fansNum, Long focusNum, String userProfile, Long postId, String postTitle, Date createTime) {
        this.id = id;
        this.userName = userName;
        this.userAccount = userAccount;
        this.userAvatar = userAvatar;
        this.fansNum = fansNum;
        this.focusNum = focusNum;
        this.userProfile = userProfile;
        this.postId = postId;
        this.postTitle = postTitle;
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public Long getFansNum() {
        return fansNum;
    }

    public void setFansNum(Long fansNum) {
        this.fansNum = fansNum;
    }

    public Long getFocusNum() {
        return focusNum;
    }

    public void setFocusNum(Long focusNum) {
        this.focusNum = focusNum;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
