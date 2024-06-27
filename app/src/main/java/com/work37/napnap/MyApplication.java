package com.work37.napnap;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化 Firebase
        FirebaseApp.initializeApp(this);
    }
}
