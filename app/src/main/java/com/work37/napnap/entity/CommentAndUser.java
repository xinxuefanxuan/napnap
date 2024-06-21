package com.work37.napnap.entity;

import java.io.Serializable;
import java.util.List;

public class CommentAndUser extends Comment implements Serializable {
    private String userName;
    private String userAvatar;

    private List<CommentAndUser> commentAndUserList;

    public CommentAndUser() {

    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String username) {
        this.userName = username;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public List<CommentAndUser> getCommentAndUserList() {
        return commentAndUserList;
    }

    public void setCommentAndUserList(List<CommentAndUser> replies) {
        this.commentAndUserList = replies;
    }
}
