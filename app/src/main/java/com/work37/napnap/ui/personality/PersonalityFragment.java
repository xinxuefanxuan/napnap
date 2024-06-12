package com.work37.napnap.ui.personality;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.work37.napnap.databinding.FragmentPersonalityBinding;
import com.work37.napnap.ui.userlogin_register.RegisterActivity;

public class PersonalityFragment extends Fragment {
    private FragmentPersonalityBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PersonalityViewModel personalityViewModel =
                new ViewModelProvider(this).get(PersonalityViewModel.class);

        binding = FragmentPersonalityBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 设置编辑资料按钮点击事件
        binding.editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到编辑个人资料页面
                // 这里可以使用Fragment的导航或者Activity的Intent跳转
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        // 观察ViewModel数据并更新UI
        personalityViewModel.getLikesandCollectionCount().observe(getViewLifecycleOwner(), count -> {
            binding.likesAndCollectionsCount.setText("获赞：" + count);
        });
        personalityViewModel.getFollowingCount().observe(getViewLifecycleOwner(), count -> {
            binding.followingCount.setText("关注：" + count);
        });
        personalityViewModel.getFollowersCount().observe(getViewLifecycleOwner(), count -> {
            binding.followersCount.setText("粉丝：" + count);
        });

        // 设置RecyclerView
//        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // 这里你可以设置适配器，例如
        // binding.recyclerView.setAdapter(new YourAdapter());

        return root;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
