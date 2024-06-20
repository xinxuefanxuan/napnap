package com.work37.napnap.ui.search;

import com.work37.napnap.entity.Game;
import com.work37.napnap.entity.Post;

import java.util.List;

public class PostResponse {
    private int code;
    private Data data;
    private String message;

    // getters and setters


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class Data {
        private List<Post> records;
        private int total;

        // getters and setters

        public List<Post> getRecords() {
            return records;
        }

        public void setRecords(List<Post> records) {
            this.records = records;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }
}
