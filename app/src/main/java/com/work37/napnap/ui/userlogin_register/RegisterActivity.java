package com.work37.napnap.ui.userlogin_register;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.work37.napnap.R;
import com.work37.napnap.databinding.ActivityMainBinding;
import com.work37.napnap.databinding.ActivityRegisterBinding;
import com.work37.napnap.global.PersistentCookieJar;
import com.work37.napnap.global.PublicActivity;
import com.work37.napnap.global.PublicApplication;
import com.work37.napnap.global.UrlConstant;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends PublicActivity {
    private ActivityRegisterBinding binding;
    private EditText UsernameInput;
    private EditText PasswordInput;
    private EditText ConfirmPasswordInput;
    private CheckBox checkBox;
    private ProgressBar ProgressBar;
    private InputMethodManager keyBoard;
    private Button RegisterButton;
    private ImageButton backButton;
    private ActivityResultLauncher<Intent> launcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UsernameInput = binding.usernameInput;
        PasswordInput = binding.passwordInput;
        ConfirmPasswordInput = binding.confirmPasswordInput;
        checkBox = binding.termsCheckbox;
        ProgressBar = binding.loading;
        RegisterButton = binding.registerButton;
        backButton = binding.backButton;
        keyBoard = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        finish();
                    }
                });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (keyBoard != null)
                    keyBoard.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                register(UsernameInput.getText().toString(), PasswordInput.getText().toString(), ConfirmPasswordInput.getText().toString());
            }
        });
    }

    private void register(String username,String password,String confirmPassword){
        if(!checkBox.isChecked()){
            Toast.makeText(getApplicationContext(), "请先同意《服务协议》和《隐私政策》", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!isUsernameValid(username)){
            Toast.makeText(getApplicationContext(), "用户名错误，请重新输入", Toast.LENGTH_SHORT).show();
            UsernameInput.setText("");
            PasswordInput.setText("");
            ConfirmPasswordInput.setText("");
            return;
        }
        if(!isPasswordValid(password)){
            Toast.makeText(getApplicationContext(), "密码错误，必须由6-12位小写字母和数字组成", Toast.LENGTH_SHORT).show();
            PasswordInput.setText("");
            ConfirmPasswordInput.setText("");
            return;
        }
        if(!isConfirmPasswordValid(password,confirmPassword)){
            Toast.makeText(getApplicationContext(), "两次密码不一致", Toast.LENGTH_SHORT).show();
            PasswordInput.setText("");
            ConfirmPasswordInput.setText("");
            return;
        }

        ProgressBar.setVisibility(View.VISIBLE);
        new Thread(()->{
            try{
                JSONObject jsonObject1 = new JSONObject();
                try {
                    jsonObject1.put("userName", username);
                    jsonObject1.put("userPassword", password);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                RequestBody requestBody = RequestBody.create(
                        jsonObject1.toString(),
                        MediaType.get("application/json; charset=utf-8")
                );

                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .cookieJar(new PersistentCookieJar(getApplicationContext()))
                        .build();
                Request request = new Request.Builder()
                        .url(UrlConstant.baseUrl+"api/user/register")
                        .post(requestBody)
                        .build();
                Response response = okHttpClient.newCall(request).execute();
                String responseBody = response.body().string();
                Log.d("aaa",responseBody);
                JSONObject jsonObject = new JSONObject(responseBody);
                int code = jsonObject.getInt("code");
                String message = jsonObject.getString("message");
                if(code==0){
                    String account = jsonObject.getString("data");
                    User user = new User(account,username,"https://gitee.com/Code_for_love/napnapimages/raw/master/-7277026446464267753头像.png");
                    PublicApplication.setCurrentUser(user);
                    runOnUiThread(()->{
                        Toast.makeText(this,"注册成功，请重新登录",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    });

                }
            }catch(Exception e){
                Log.d("bbb","响应失败");
                e.printStackTrace();
            }
        }).start();
    }

    private boolean isUsernameValid(String username){
        if(username.isEmpty()){
            return false;
        }
        return true;
    }

    private boolean isPasswordValid(String password){
        if(password.matches("^[a-z0-9]{6,12}$")){
            return true;
        }
        return false;
    }

    private boolean isConfirmPasswordValid(String password,String confirmPassword){
        if(confirmPassword.equals(password)){
            return true;
        }
        return false;
    }
}
