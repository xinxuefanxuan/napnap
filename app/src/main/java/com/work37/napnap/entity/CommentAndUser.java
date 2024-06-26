package com.work37.napnap.entity;

import com.work37.napnap.ui.userlogin_register.User;

import java.io.Serializable;
import java.util.List;

public class CommentAndUser extends Comment implements Serializable {
    private String userName;
    private String userAvatar;
    private User parentUser;

    public User getParentUser() {
        return parentUser;
    }

    public void setParentUser(User parentUser) {
        this.parentUser = parentUser;
    }




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
