package com.work37.napnap.ui.message.b_MessageNewFav;

import com.work37.napnap.RequestAndResponse.UserResponse;
import com.work37.napnap.ui.userlogin_register.User;

import java.util.List;

public class NewFavResponse {
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
        private List<NewFav> records;
        private int total;

        // getters and setters

        public List<NewFav> getRecords() {
            return records;
        }

        public void setRecords(List<NewFav> records) {
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
