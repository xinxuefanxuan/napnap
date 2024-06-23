package com.work37.napnap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationBarView;
import com.work37.napnap.databinding.ActivityMainBinding;
import com.work37.napnap.global.PublicActivity;
import com.work37.napnap.global.PublicApplication;
import com.work37.napnap.ui.userlogin_register.LoginActivity;
import com.work37.napnap.ui.userlogin_register.LoginReponse;
import com.work37.napnap.ui.userlogin_register.LoginViewModel;
import com.work37.napnap.ui.userlogin_register.LoginViewModelFactory;
import com.work37.napnap.ui.userlogin_register.User;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends PublicActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;

    //用于从后端获取数据
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initBottomNavigation();
        loginViewModel = new ViewModelProvider(this,new LoginViewModelFactory(getApplicationContext()))
                .get(LoginViewModel.class);
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
                    Toast.makeText(getApplicationContext(),"用户已登录",Toast.LENGTH_SHORT).show();
                }else{
                    navigateToLoginActivity();
                    Toast.makeText(getApplicationContext(),"用户未登录，请先登录",Toast.LENGTH_SHORT).show();
                    Log.d("ddd","登录失败");
                }

            }
        });
    }

}