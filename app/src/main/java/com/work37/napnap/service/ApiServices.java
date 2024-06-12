package com.work37.napnap.service;

import com.work37.napnap.global.GlobalResponse;
import com.work37.napnap.ui.userlogin_register.LoginRequest;
import com.work37.napnap.ui.userlogin_register.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiServices {
    @POST("login")
    Call<GlobalResponse> login(@Body LoginRequest request);

    @POST("register")
    Call<GlobalResponse> register(@Body RegisterRequest request);

    @GET("getLoginUser")
    Call<GlobalResponse> isLogin();
}
