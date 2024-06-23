package com.work37.napnap.ui.community;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.work37.napnap.R;
import com.work37.napnap.databinding.FragmentCommunityBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CommunityFragment extends Fragment {

    private FragmentCommunityBinding binding;
    private Set<String> selectedTags = new HashSet<>();

    private TagFilterPostListFragment tagFilterPostListFragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CommunityViewModel communityViewModel =
                new ViewModelProvider(this).get(CommunityViewModel.class);
        binding = FragmentCommunityBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        // Initialize RecyclerView with TagFilteredGameListFragment
        if (savedInstanceState == null) {
            tagFilterPostListFragment = TagFilterPostListFragment.newInstance(new ArrayList<>(selectedTags));
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.findgamePager, tagFilterPostListFragment)
                    .commit();
        } else {
            tagFilterPostListFragment = (TagFilterPostListFragment) getChildFragmentManager().findFragmentById(R.id.findgamePager);
        }
        setupCategoryButtons();
        binding.fabAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),AddPostActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }
    private void setupCategoryButtons() {
        // Set click listeners for all category buttons
        binding.tvCategory.setOnClickListener(v -> {
            selectedTags.clear();
            updateGameList();
            updateButtonStates();
        });
        binding.btnTechnology.setOnClickListener(v -> {
            toggleTag("技术", binding.btnTechnology);
        });
        binding.btnCulture.setOnClickListener(v -> {
            toggleTag("文化", binding.btnCulture);
        });
        binding.btnAmuse.setOnClickListener(v -> {
            toggleTag("娱乐", binding.btnAmuse);
        });
        binding.btnFood.setOnClickListener(v -> {
            toggleTag("美食", binding.btnFood);
        });
        binding.btnEducation.setOnClickListener(v -> {
            toggleTag("教育", binding.btnEducation);
        });
        binding.btnGame.setOnClickListener(v -> {
            toggleTag("游戏", binding.btnGame);
        });
        binding.btnHealth.setOnClickListener(v -> {
            toggleTag("健康", binding.btnHealth);
        });
        binding.btnNew.setOnClickListener(v -> {
            toggleTag("新闻", binding.btnNew);
        });
        binding.btnSports.setOnClickListener(v -> {
            toggleTag("运动", binding.btnSports);
        });
        binding.btnTravel.setOnClickListener(v -> {
            toggleTag("旅游", binding.btnTravel);
        });
    }

    //    @SuppressLint("ResourceAsColor")
    @SuppressLint("ResourceAsColor")
    private void toggleTag(String tag, Button button) {
        if (selectedTags.contains(tag)) {
            selectedTags.remove(tag);
            button.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.default_button_color)); // 恢复默认颜色
        } else {
            selectedTags.add(tag);
            button.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.selected_button_color)); // 选中颜色
        }
        updateGameList();
        if (selectedTags.isEmpty()) {
            binding.tvCategory.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.selected_button_color));
        } else {
            binding.tvCategory.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.default_button_color));
        }
    }

    private void updateGameList() {
        if (tagFilterPostListFragment != null) {
            tagFilterPostListFragment.updateTags(new ArrayList<>(selectedTags));
        }
    }
    private void updateButtonStates() {
        // 更新所有按钮的颜色状态
        binding.tvCategory.setBackgroundColor(selectedTags.isEmpty() ? ContextCompat.getColor(getContext(), R.color.selected_button_color): ContextCompat.getColor(getContext(), R.color.default_button_color));
        updateButtonState(binding.btnTechnology, "技术");
        updateButtonState(binding.btnCulture, "文化");
        updateButtonState(binding.btnAmuse, "娱乐");
        updateButtonState(binding.btnFood, "美食");
        updateButtonState(binding.btnEducation, "教育");
        updateButtonState(binding.btnGame, "游戏");
        updateButtonState(binding.btnHealth, "健康");
        updateButtonState(binding.btnNew, "新闻");
        updateButtonState(binding.btnSports, "运动");
        updateButtonState(binding.btnTravel, "旅游");
    }

    @SuppressLint("ResourceAsColor")
    private void updateButtonState(Button button, String tag) {
        if (selectedTags.contains(tag)) {
            button.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.selected_button_color));
        } else {
            button.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.default_button_color));
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}