package com.work37.napnap.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Comment implements Serializable {
    private Long id;
    private Long uid;
    private Long parentId;
    private Integer commentType;//0是评论帖子，1是评论评论
    private String content;
    private List<String> picture;
    private Date createTime;

    public Comment() {
    }

    public Comment(Long postId, Long uId, Long parentId, Integer commentType, String content, List<String> picture, Date createTime) {
        this.id = postId;
        this.uid = uId;
        this.parentId = parentId;
        this.commentType = commentType;
        this.content = content;
        this.picture = picture;
        this.createTime = createTime;
    }

    public Long getPostId() {
        return id;
    }

    public void setPostId(Long postId) {
        this.id = postId;
    }

    public Long getuId() {
        return uid;
    }

    public void setuId(Long uId) {
        this.uid = uId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getCommentType() {
        return commentType;
    }

    public void setCommentType(Integer commentType) {
        this.commentType = commentType;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
