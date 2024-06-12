package com.work37.napnap.ui.userlogin_register;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.work37.napnap.MainActivity;
import com.work37.napnap.R;
import com.work37.napnap.global.GlobalResponse;
import com.work37.napnap.global.PublicActivity;
import com.work37.napnap.service.ApiServices;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends PublicActivity {
    private EditText usernameInput;
    private EditText passwordInput;
    private CheckBox checkBox;
    private Button registerButton;
    private Button loginButton;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        checkBox = findViewById(R.id.termsCheckbox);
        registerButton = findViewById(R.id.registerButton);
        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v->login());
        registerButton.setOnClickListener(v->goToRegister());
        sharedPref = LoginActivity.this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    public void goToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void login() {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();
        boolean checked = checkBox.isChecked();
        if (!checked) {
            Toast.makeText(this, "请先同意用户协议", Toast.LENGTH_SHORT).show();
            return;
        }

        MutableLiveData<GlobalResponse> loginResponse = new MutableLiveData<>();
        LoginLinkEnd loginLinkEnd = new LoginLinkEnd(this, loginResponse);
        loginLinkEnd.login(username, password);

        loginResponse.observe(this, response -> {
            if (response != null && response.getCode() == 0) {
                navigateToMainActivity();
            } else {
                Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}


