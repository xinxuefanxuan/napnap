package com.work37.napnap.global;

import org.json.JSONObject;

public class GlobalResponse<T> {
    private int code;
    private JSONObject data;
    private String message;

    public GlobalResponse() {
    }

    public GlobalResponse(int code, JSONObject data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
