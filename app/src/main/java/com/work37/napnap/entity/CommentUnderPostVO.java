package com.work37.napnap.entity;

import java.io.Serializable;
import java.util.List;

public class CommentUnderPostVO extends Comment implements Serializable {
    private List<CommentUnderPostVO> replies;

    public CommentUnderPostVO(List<CommentUnderPostVO> replies) {
        this.replies = replies;
    }

    public List<CommentUnderPostVO> getReplies() {
        return replies;
    }

    public void setReplies(List<CommentUnderPostVO> replies) {
        this.replies = replies;
    }
}
