package com.work37.napnap.ui.message.b_MessageNewReply;

import com.work37.napnap.ui.message.b_MessageNewFav.NewFav;
import com.work37.napnap.ui.message.b_MessageNewFav.NewFavResponse;

import java.util.List;

public class NewReplyResponse {
    private int code;
    private Data data;
    private String message;


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
        private List<NewReply> records;
        private int total;

        // getters and setters

        public List<NewReply> getRecords() {
            return records;
        }

        public void setRecords(List<NewReply> records) {
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
