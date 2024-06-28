package com.work37.napnap.ui.personality;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.work37.napnap.R;
import com.work37.napnap.databinding.FragmentPersonalityBinding;
import com.work37.napnap.global.PersistentCookieJar;
import com.work37.napnap.global.PublicApplication;
import com.work37.napnap.global.UrlConstant;
import com.work37.napnap.ui.userlogin_register.LoginActivity;
import com.work37.napnap.ui.userlogin_register.LoginViewModel;
import com.work37.napnap.ui.userlogin_register.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
            personalityViewModel.setUsername(user.getUserName());
            personalityViewModel.setUserAccount(user.getUserAccount());
            personalityViewModel.setFollowingCount(user.getFollowNum());
            personalityViewModel.setFollowersCount(user.getFanNum());
            personalityViewModel.setUserAvatar(user.getUserAvatar());
            binding.userAccount.setText(user.getUserAccount());
            binding.username.setText(user.getUserName());
            binding.followersCount.setText("粉丝:"+user.getFanNum());
            binding.followingCount.setText("关注:"+user.getFollowNum());

            if(user.getUserAvatar().equals("")){
                user.setUserAvatar("https://gitee.com/Code_for_love/napnapimages/raw/master/-7277026446464267753头像.png");
            }
            // 确保在 UI 线程中进行 Glide 加载操作
            new Handler(Looper.getMainLooper()).post(() -> {
                binding.userAvatar.setRoundedImage(user.getUserAvatar(),20,5, R.color.red);
            });
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

                User user = PublicApplication.getCurrentUser();
                if (user.getFollowNum()==0){
                    Toast.makeText(getActivity(),"没有关注", Toast.LENGTH_LONG).show();
                }
                Intent intent = new Intent(getContext(), FollowActivity.class);
                startActivity(intent);
            }
        });

        binding.followersCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = PublicApplication.getCurrentUser();
                Long fanNum = user.getFanNum();
                if (fanNum==0){
                    Toast.makeText(getParentFragment().getActivity(), "没有粉丝", Toast.LENGTH_LONG).show();
                }
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
                performLogout();
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
            binding.username.setText(user.getUserName());
            binding.userAccount.setText(user.getUserAccount());
            binding.followersCount.setText("粉丝:"+user.getFanNum());
            binding.followingCount.setText("关注:"+user.getFollowNum());
            binding.viewProfileButton.setEnabled(true);//查看资料按钮不可点击
            binding.btnCollectionApp.setEnabled(true);
            binding.btnCollectionPost.setEnabled(true);
            binding.btnPost.setEnabled(true);
            binding.btnPasswordChange.setEnabled(true);
            binding.btnLayout.setEnabled(true);

            // 可以在这里做其他需要更新的UI操作
        }
    }
    //退出登录
    private void performLogout(){
        new Thread(()->{
        try{
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cookieJar(new PersistentCookieJar(getContext()))
                    .build();
            Request request = new Request.Builder()
                    .url(UrlConstant.baseUrl+"api/user/logout")
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            String responseBody = response.body().string();
            Log.d("aaa",responseBody);
            JSONObject jsonObject = new JSONObject(responseBody);
            int code = jsonObject.getInt("code");
            String message = jsonObject.getString("message");
            if(code==0){
                new Handler(Looper.getMainLooper()).post(() -> {
                    PublicApplication.setCurrentUser(null);
                    Toast.makeText(getContext(),"退出登录成功",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(),LoginActivity.class);
                    startActivity(intent);
                });
            }else{
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(getContext(),"退出登录失败",Toast.LENGTH_SHORT).show();
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        }).start();

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
