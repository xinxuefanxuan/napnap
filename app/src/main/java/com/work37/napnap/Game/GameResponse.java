package com.work37.napnap.Game;

import com.work37.napnap.entity.Game;

import java.util.List;

public class GameResponse {
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
        private List<Game> records;
        private int total;

        // getters and setters

        public List<Game> getRecords() {
            return records;
        }

        public void setRecords(List<Game> records) {
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
