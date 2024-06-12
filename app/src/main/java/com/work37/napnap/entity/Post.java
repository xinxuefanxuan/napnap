package com.work37.napnap.entity;

import android.graphics.drawable.Drawable;

public class Post {
    private int postId;
    private int userId;
    private String title;
    private String content;
    private Drawable picture;
    private String tags;
    private int likesCount;
    private int collectCount;

    public Post() {
    }

    public Post(int postId,int userId, String title, String content, Drawable picture, String tags, int likesCount, int collectCount) {
        this.postId = postId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.picture = picture;
        this.tags = tags;
        this.likesCount = likesCount;
        this.collectCount = collectCount;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Drawable getPicture() {
        return picture;
    }

    public void setPicture(Drawable picture) {
        this.picture = picture;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(int collectCount) {
        this.collectCount = collectCount;
    }
}
