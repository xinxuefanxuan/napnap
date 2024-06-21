package com.work37.napnap.Game;

import com.work37.napnap.entity.CommentUnderPostVO;

import java.util.List;

public class PostResponse {
    private int code;
    private CommentData data;
    private String message;

    public int getCode() {
        return code;
    }

    public CommentData getCommentData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public static class CommentData{
       private int total;
       private List<CommentUnderPostVO> records;

        public CommentData() {
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<CommentUnderPostVO> getRecords() {
            return records;
        }

        public void setRecords(List<CommentUnderPostVO> records) {
            this.records = records;
        }
    }
}
