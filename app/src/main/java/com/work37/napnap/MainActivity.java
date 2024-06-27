package com.work37.napnap;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.work37.napnap.databinding.ActivityMainBinding;
import com.work37.napnap.global.PersistentCookieJar;
import com.work37.napnap.global.PublicActivity;
import com.work37.napnap.global.PublicApplication;
import com.work37.napnap.global.UrlConstant;
import com.work37.napnap.ui.userlogin_register.LoginActivity;
import com.work37.napnap.ui.userlogin_register.LoginReponse;
import com.work37.napnap.ui.userlogin_register.LoginViewModel;
import com.work37.napnap.ui.userlogin_register.LoginViewModelFactory;
import com.work37.napnap.ui.userlogin_register.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends PublicActivity {


    private ActivityMainBinding binding;

    private boolean isFirstLoading;

    //用于从后端获取数据
    private LoginViewModel loginViewModel;
    private boolean sendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initBottomNavigation();
        loginViewModel = new ViewModelProvider(this,new LoginViewModelFactory(getApplicationContext()))
                .get(LoginViewModel.class);
        isFirstLoading = true;
        sendToken = false;
        getLoginUser();




        // 获取并显示 FCM 令牌
        getAndDisplayFcmToken();

    }


    private static final String PREFS_NAME = "FCM_PREFS";
    private static final String TOKEN_KEY = "FCM_TOKEN";

    private void getAndDisplayFcmToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("MainActivity", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        // 获取新令牌
                        String token = task.getResult();
                        Long id = PublicApplication.getCurrentUser().getId();
                        if(id==null){
                            SendToken(token,null);
                        }else{
                            SendToken(token,id);
                        }
                        Log.d("MainActivity", "FCM Token: " + token);
                        storeTokenInSharedPreferences(token);
                        showToast(token);
                        Log.d("TAG", "Refreshed token: " + token);

                    }
                });
    }

    private void SendToken(String token,Long userId){
        new Thread(()->{
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId",userId);
                jsonObject.put("userToken",token);
                RequestBody requestBody = RequestBody.create(
                        jsonObject.toString(),
                        MediaType.get("application/json; charset=utf-8")
                );

                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .cookieJar(new PersistentCookieJar(getApplicationContext()))
                        .build();
                Request request = new Request.Builder()
                        .url(UrlConstant.baseUrl+"api/token/isExistToken")
                        .post(requestBody)
                        .build();
                Response response = okHttpClient.newCall(request).execute();
                String responseBody = response.body().string();
                Log.d("aaa",responseBody);
                JSONObject jsonObject1 = new JSONObject(responseBody);
                int code = jsonObject1.getInt("code");
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();

    }

    private void storeTokenInSharedPreferences(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }

    private void showToast(String token) {
        Toast.makeText(this, "FCM Token: " + token, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoginUser();
    }

    //初始化底部导航栏
    private void initBottomNavigation(){
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    //从后端获取当前用户信息
    private void getLoginUser(){
        loginViewModel.isLogin();
        loginViewModel.getLoginReponseMutableLiveData().observe(this, new Observer<LoginReponse>() {
            @Override
            public void onChanged(LoginReponse loginReponse) {
                if(loginReponse==null){
                    navigateToLoginActivity();
                }
                Log.d("eee",loginReponse.toString());

                if(loginReponse.isResultState()){
                    if(isFirstLoading){
                        Toast.makeText(getApplicationContext(),"用户已登录",Toast.LENGTH_SHORT).show();
                        isFirstLoading = false;
                    }
                }else{
                    navigateToLoginActivity();
//                    Toast.makeText(getApplicationContext(),"用户未登录，请先登录",Toast.LENGTH_SHORT).show();
                    Log.d("ddd","登录失败");
                }

            }
        });
    }




}