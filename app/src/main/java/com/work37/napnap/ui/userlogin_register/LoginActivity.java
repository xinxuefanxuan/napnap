package com.work37.napnap.ui.userlogin_register;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.work37.napnap.MainActivity;
import com.work37.napnap.R;
import com.work37.napnap.databinding.ActivityLoginBinding;
import com.work37.napnap.global.GlobalResponse;
import com.work37.napnap.global.PublicActivity;
import com.work37.napnap.global.PublicApplication;
import com.work37.napnap.ui.personality.PersonalityFragment;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends PublicActivity {

    private static final String TAG = "LoginActivity";
    private ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;
    private EditText usernameInput;
    private EditText passwordInput;
    private CheckBox checkBox;
    private Button registerButton;
    private Button loginButton;

//    private SharedPreferences preferences;

    private ProgressBar progressBar;

//    private UserDao userDao;
    private ImageButton goBackButton;
    private ActivityResultLauncher<Intent> launcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //preferences = LoginActivity.this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        setContentView(binding.getRoot());
        loginViewModel = new ViewModelProvider(this,new LoginViewModelFactory(getApplicationContext()))
                .get(LoginViewModel.class);
        usernameInput = binding.usernameInput;
        passwordInput = binding.passwordInput;
        checkBox = binding.termsCheckbox;
        progressBar = binding.loading;
        registerButton = binding.registerButton;
        loginButton = binding.loginButton;
        goBackButton = binding.backButton;
        InputMethodManager keyBoard = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        usernameInput.setText(preferences.getString("user",""));

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        finish();
                    }
                });
        loginViewModel.getLoginStateMutableLiveData().observe(this, new Observer<LoginState>() {
            @SuppressLint("ResourceType")
            @Override
            public void onChanged(@Nullable LoginState loginState) {
                if(loginState==null){
                    return;
                }
                loginButton.setEnabled(loginState.isValid());
            }

        });

        loginViewModel.getLoginReponseMutableLiveData().observe(this, new Observer<LoginReponse>() {
            @Override
            public void onChanged(LoginReponse loginReponse) {
                if(loginReponse==null){
                    return;
                }
                Log.d("eee",loginReponse.toString());
                progressBar.setVisibility(View.GONE);

                if(loginReponse.isResultState()){
                    Log.d("ccc","登陆成功");
                    JSONObject data = loginReponse.getData();
                    try {
                        User user = new User(data.getLong("id"),data.getString("userName"),data.getLong("fansNum")
                        ,data.getLong("focusNum"),data.getString("userProfile"),data.getString("userAvatar"));
                        user.setUserAccount(data.getString("userAccount"));
                        PublicApplication.setCurrentUser(user);
                        setResult(Activity.RESULT_OK,new Intent());
                        finish();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    Log.d("ddd","登录失败");
                    showLoginFailed(loginReponse.getErrorMsg());
                }
            }
        });
        if(PublicApplication.getCurrentUser()!=null&&PublicApplication.getCurrentUser().getUserAccount()!=null){
            usernameInput.setText(PublicApplication.getCurrentUser().getUserAccount());
        }

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.LoginDataChange(usernameInput.getText().toString(),
                        passwordInput.getText().toString());
            }
        };
        usernameInput.addTextChangedListener(afterTextChangedListener);
        passwordInput.addTextChangedListener(afterTextChangedListener);
       passwordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (keyBoard != null)
                        keyBoard.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    if (!checkBox.isChecked()) {
                        Toast.makeText(getApplicationContext(), "请先同意《服务协议》和《隐私政策》", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    loginViewModel.login(usernameInput.getText().toString(),
                            passwordInput.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (keyBoard != null)
                    keyBoard.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                if (!checkBox.isChecked()) {
                    Toast.makeText(getApplicationContext(), "请先同意《服务协议》和《隐私政策》", Toast.LENGTH_SHORT).show();
                    return;
                }

               progressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameInput.getText().toString(),
                        passwordInput.getText().toString());
            }
        });

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                launcher.launch(intent);
            }
        });

    }

//    private void updateUiWithUser(JSONObject data) {
//        String welcome = null;
//        try {
//            welcome = getString(R.string.welcome) + new JSONObject(data.getString("userName"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
////        new Thread(new Runnable() {
////            @Override
////            public void run() {
////                userDao.deleteUser();
////                userDao.insertUser(new User(data));
////            }
////        }).start();
//        SharedPreferences.Editor editor = preferences.edit();
//        String userJson = new Gson().toJson(data);
//        editor.putString("user",userJson);
//        editor.apply();
//        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_SHORT).show();
//    }

    private void showLoginFailed(String errorString) {
        Toast.makeText(this, errorString, Toast.LENGTH_SHORT).show();
    }

}


