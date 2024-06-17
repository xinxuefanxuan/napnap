package com.work37.napnap.ui.personality;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.work37.napnap.databinding.FragmentPersonalityBinding;
import com.work37.napnap.global.PublicApplication;
import com.work37.napnap.ui.userlogin_register.LoginActivity;
import com.work37.napnap.ui.userlogin_register.LoginViewModel;
import com.work37.napnap.ui.userlogin_register.User;

import org.json.JSONException;

public class PersonalityFragment extends Fragment {
    private FragmentPersonalityBinding binding;
    private PersonalityViewModel personalityViewModel;
    private LoginViewModel loginViewModel;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        personalityViewModel = new ViewModelProvider(this).get(PersonalityViewModel.class);
        // 获取ViewModel
        personalityViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                if (modelClass.isAssignableFrom(PersonalityViewModel.class)) {
                    return (T) new PersonalityViewModel(personalityRepository.getInstance(getContext()));
                }
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }).get(PersonalityViewModel.class);
        binding = FragmentPersonalityBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 处理用户登录状态
        if (PublicApplication.getCurrentUser() == null) {
            Toast.makeText(getContext(), "用户未登录，请先登录", Toast.LENGTH_SHORT).show();
            binding.viewProfileButton.setEnabled(false);//查看资料按钮不可点击
            binding.clickToLogin.setVisibility(View.VISIBLE);
            binding.btnCollectionApp.setEnabled(false);
            binding.btnCollectionPost.setEnabled(false);
            binding.btnPost.setEnabled(false);
            binding.btnPasswordChange.setEnabled(false);
            binding.btnLayout.setEnabled(false);
            binding.clickToLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            User user = PublicApplication.getCurrentUser();
            personalityViewModel.setUsername(user.getUsername());
            personalityViewModel.setUserAccount(user.getUid());
            personalityViewModel.setFollowingCount(user.getFollowNum());
            personalityViewModel.setFollowersCount(user.getFanNum());
            binding.btnCollectionApp.setEnabled(true);
            binding.btnCollectionPost.setEnabled(true);
            binding.btnPost.setEnabled(true);
            binding.btnPasswordChange.setEnabled(true);
            binding.btnLayout.setEnabled(true);
        }

        // 设置查看资料按钮点击事件
        binding.viewProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });


        binding.followingCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowActivity.class);
                startActivity(intent);
            }
        });

        binding.followersCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),FansActivity.class);
                startActivity(intent);
            }
        });

        // 设置按钮点击事件
        binding.btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserPostsActivity.class);
                startActivity(intent);
            }
        });

        binding.btnCollectionPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CollectedPostsActivity.class);
                startActivity(intent);
            }
        });

        binding.btnCollectionApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CollectedAppsActivity.class);
                startActivity(intent);
            }
        });

        binding.btnPasswordChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        // 设置按钮点击事件
        binding.btnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    performLogout();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // 观察注销结果
        personalityViewModel.getLogoutResult().observe(getViewLifecycleOwner(), result -> {
            if (result) {
                Toast.makeText(getContext(), "退出登录成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                requireActivity().finish(); // 确保当前活动被关闭
            } else {
                Toast.makeText(getContext(), "退出登录失败", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }
    @Override
    public void onResume() {
        super.onResume();

        // 检查当前用户是否已登录
        if (PublicApplication.getCurrentUser() == null) {
            // 用户未登录的处理逻辑
            Toast.makeText(getContext(), "用户未登录，请先登录", Toast.LENGTH_SHORT).show();
            binding.viewProfileButton.setEnabled(false);
            binding.clickToLogin.setVisibility(View.VISIBLE);
            binding.btnCollectionApp.setEnabled(false);
            binding.btnCollectionPost.setEnabled(false);
            binding.btnPost.setEnabled(false);
            binding.btnPasswordChange.setEnabled(false);
            binding.btnLayout.setEnabled(false);
            binding.clickToLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            // 用户已登录，更新 UI 显示用户信息
            User user = PublicApplication.getCurrentUser();
            binding.username.setText(user.getUsername());
            binding.userAccount.setText(String.valueOf(user.getUid()));
            binding.followingCount.setText(String.valueOf(user.getFollowNum()));
            binding.followersCount.setText(String.valueOf(user.getFanNum()));
            binding.viewProfileButton.setEnabled(true);//查看资料按钮不可点击
//            personalityViewModel.setUsername(user.getUsername());
//            personalityViewModel.setUserAccount(user.getUid());
//            personalityViewModel.setFollowingCount(user.getFollowNum());
//            personalityViewModel.setFollowersCount(user.getFanNum());
            binding.btnCollectionApp.setEnabled(true);
            binding.btnCollectionPost.setEnabled(true);
            binding.btnPost.setEnabled(true);
            binding.btnPasswordChange.setEnabled(true);
            binding.btnLayout.setEnabled(true);

            // 可以在这里做其他需要更新的UI操作
        }
    }

    //退出登录
    private void performLogout() throws JSONException {
        personalityViewModel.logout();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
