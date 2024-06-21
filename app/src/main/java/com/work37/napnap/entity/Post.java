package com.work37.napnap.entity;

import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Post implements Serializable {

    private Long id;//帖子id
    private Long userId;//发帖用户id
    private String title;//标题
    private String content;//内容
    private List<String> pictures;//图片地址
    private List<String> tag;
    private int likes;
    private int collectNum;

    private Date createTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
//    private Date createDate;

    public Post() {
    }

    public Post(Long id, Long userId, String title, String content, List<String> pictures, List<String> tag, int likes, int collectNum, Date createTime) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.pictures = pictures;
        this.tag = tag;
        this.likes = likes;
        this.collectNum = collectNum;
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
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

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
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
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", pictures=" + pictures +
                ", tag=" + tag +
                ", likes=" + likes +
                ", collectNum=" + collectNum +
                ", createTime=" + createTime +
                '}';
    }
}
