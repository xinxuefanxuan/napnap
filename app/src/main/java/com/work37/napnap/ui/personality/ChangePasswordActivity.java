package com.work37.napnap.ui.personality;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.work37.napnap.R;
import com.work37.napnap.databinding.ActivityChangePasswordBinding;
import com.work37.napnap.global.PersistentCookieJar;
import com.work37.napnap.global.PublicActivity;
import com.work37.napnap.global.UrlConstant;
import com.work37.napnap.ui.userlogin_register.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangePasswordActivity extends PublicActivity {

    private ActivityChangePasswordBinding binding;

    //新密码
    private TextInputEditText NewPassword;
    //确认密码
    private TextInputEditText ConfirmPassword;
    //点击确认密码按钮
    private Button btnChangePassword;
    //进度条转圈圈
    private ProgressBar progressBar;
    //返回按钮
    private ImageButton backButton;

    private ActivityResultLauncher<Intent> loginActivityResultLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        NewPassword = binding.etNewPassword;
        ConfirmPassword = binding.etConfirmPassword;
        btnChangePassword = binding.btnChangePassword;
        backButton = binding.backButton;
        progressBar = binding.progressBar;

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkInputFields();
            }
        };

        NewPassword.addTextChangedListener(textWatcher);
        ConfirmPassword.addTextChangedListener(textWatcher);

        btnChangePassword.setOnClickListener(v -> changePassword());

        // 初始化 ActivityResultLauncher
        loginActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != RESULT_OK) {
                        Toast.makeText(this, "需要先进行登录", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        // 用户成功登录，允许进行密码修改操作
                        checkInputFields();
                    }
                }
        );

        // 跳转到登录页面进行登录操作
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginActivityResultLauncher.launch(loginIntent);

    }

    private void changePassword() {
        String newPassword = NewPassword.getText().toString().trim();
        String confirmPassword = ConfirmPassword.getText().toString().trim();

        if (!validateNewPassword(newPassword)) {
            NewPassword.setError("新密码不符合格式");
            return;
        } else {
            NewPassword.setError(null);
        }

        if (!newPassword.equals(confirmPassword)) {
            ConfirmPassword.setError("两次密码不一致");
            ConfirmPassword.setText("");
            return;
        } else {
            ConfirmPassword.setError(null);
        }
        changePassword(newPassword);

    }
    private void changePassword(String newPassword) {
        JSONObject json = new JSONObject();
        try {
            json.put("userPassword", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(new PersistentCookieJar(getApplicationContext()))
                    .build();
            RequestBody requestBody = RequestBody.create(
                    json.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );
            Request request = new Request.Builder()
                    .url(UrlConstant.baseUrl + "/api/user/updateUserPassword")
                    .put(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                int code = jsonObject.getInt("code");
                String message = jsonObject.getString("message");

                // 在主线程上处理UI更新
                runOnUiThread(() -> {
                    if (code == 0) {
                        Toast.makeText(getApplicationContext(), "成功修改密码", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        btnChangePassword.setEnabled(true);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    btnChangePassword.setEnabled(true);
                });
            }
        }).start();
    }



    private boolean validateNewPassword(String newPassword) {
        // 检查新密码是否符合格式
        return newPassword.matches("^[0-9a-z]{6,12}$"); // 示例验证：密码长度至少为6
    }


    private void checkInputFields() {
        String newPassword = NewPassword.getText().toString().trim();
        String confirmPassword = ConfirmPassword.getText().toString().trim();

        btnChangePassword.setEnabled(!newPassword.isEmpty() && !confirmPassword.isEmpty());
    }
}
