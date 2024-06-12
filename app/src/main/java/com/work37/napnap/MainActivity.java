package com.work37.napnap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationBarView;
import com.work37.napnap.databinding.ActivityMainBinding;
import com.work37.napnap.global.GlobalResponse;
import com.work37.napnap.global.PublicActivity;
import com.work37.napnap.global.PublicApplication;
import com.work37.napnap.service.ApiServices;
import com.work37.napnap.ui.userlogin_register.LoginActivity;
import com.work37.napnap.ui.userlogin_register.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends PublicActivity {

    private ActivityMainBinding binding;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        setContentView(binding.getRoot());

        initBottomNavigation();
        if(sharedPreferences.contains("user")){
            Log.d("aaaaa","已经登录");
        }else{
            Toast.makeText(this, "用户未登录，请先进行登录", Toast.LENGTH_SHORT).show();
            navigateToLoginActivity();
        }
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

}