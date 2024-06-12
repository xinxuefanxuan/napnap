package com.work37.napnap.ui.personality;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.work37.napnap.databinding.ActivityEditProfileBinding;
import com.work37.napnap.global.PublicActivity;

public class EditProfileActivity extends PublicActivity {
    private ActivityEditProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 设置后退按钮点击事件
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // 调用系统默认的回退逻辑
            }
        });

        // 设置保存按钮点击事件
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 保存编辑的数据
                // 这里你可以将数据发送到ViewModel或后端
            }
        });
    }

}
