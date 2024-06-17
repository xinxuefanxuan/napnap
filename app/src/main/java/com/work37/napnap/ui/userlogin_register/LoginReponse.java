package com.work37.napnap.ui.userlogin_register;

import org.json.JSONObject;

public class  LoginReponse {
    private boolean resultState;
    private String errorMsg;
    private JSONObject data;
    LoginReponse(JSONObject data){
        this.data = data;
        this.resultState=true;
    }
    LoginReponse(String errorMsg){
        this.errorMsg=errorMsg;
        this.resultState=false;
    }
    public boolean isResultState() {
        return resultState;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public JSONObject getData() {
        return data;
    }

    @Override
    public String toString() {
        return "LoginReponse{" +
                "resultState=" + resultState +
                ", errorMsg='" + errorMsg + '\'' +
                ", data=" + data +
                '}';
    }
}
