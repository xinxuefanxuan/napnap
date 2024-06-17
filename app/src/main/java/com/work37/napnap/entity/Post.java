package com.work37.napnap.entity;

import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Post implements Serializable { ;
    private int userId;//发帖用户id
    private String title;//标题
    private String content;//内容
    private List<String> picture;//图片地址
    private List<String> tag;
    private int likes;
    private int collectNum;

//    private Date createDate;

    public Post() {
    }

    public Post(int userId, String title, String content, List<String> picture, List<String> tag, int likes, int collectNum) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.picture = picture;
        this.tag = tag;
        this.likes = likes;
        this.collectNum = collectNum;
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

    public List<String> getPicture() {
        return picture;
    }

    public void setPicture(List<String> picture) {
        this.picture = picture;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getCollectNum() {
        return collectNum;
    }

    public void setCollectNum(int collectNum) {
        this.collectNum = collectNum;
    }

    @Override
    public String toString() {
        return "Post{" +
                "userId=" + userId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", picture=" + picture +
                ", tag=" + tag +
                ", likes=" + likes +
                ", collectNum=" + collectNum +
                '}';
    }
}
