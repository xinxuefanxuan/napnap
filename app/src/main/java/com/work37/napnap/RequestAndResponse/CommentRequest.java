package com.work37.napnap.RequestAndResponse;

import java.util.ArrayList;

public class CommentRequest {
    private Long parentId;
    private String content;
    private ArrayList<String> picture;
    private int type;

    public CommentRequest() {
    }

    public CommentRequest(Long parentId, String comment, ArrayList<String> picture, int type) {
        this.parentId = parentId;
        this.content = comment;
        this.picture = picture;
        this.type = type;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getComment() {
        return content;
    }

    public void setComment(String comment) {
        this.content = comment;
    }

    public ArrayList<String> getPicture() {
        return picture;
    }

    public void setPicture(ArrayList<String> picture) {
        this.picture = picture;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
