package com.work37.napnap.ui.userlogin_register;

import android.os.Bundle;
import android.view.View;

import com.work37.napnap.R;
import com.work37.napnap.databinding.ActivityMainBinding;
import com.work37.napnap.databinding.ActivityRegisterBinding;
import com.work37.napnap.global.PublicActivity;

public class RegisterActivity extends PublicActivity {
    private ActivityRegisterBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    public void goBack(View view) {
        finish(); // Finish the current activity and go back to the previous one
    }

    public void register(View view) {
        // Implement registration logic
    }
}
