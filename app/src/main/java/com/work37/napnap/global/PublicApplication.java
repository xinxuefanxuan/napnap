package com.work37.napnap.global;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.work37.napnap.ui.userlogin_register.User;
import com.work37.napnap.ui.userlogin_register.UserDatabase;

public class PublicApplication extends Application {

    public static final String EXTRA_MESSAGE = "INTENT_EXTRA";
    private static Context mContext;

    private static UserDatabase userDatabase;

    private static User currentUser = null;

    public static Context getInstance() {
        return mContext;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        userDatabase = UserDatabase.getInstance(this);
    }

    public static Context getContext() {
        return mContext;
    }

    public static UserDatabase getUserDatabase() {
        return userDatabase;
    }

    // 设置当前登录用户信息
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    // 获取当前登录用户信息
    public static User getCurrentUser() {
        return currentUser;
    }
}
