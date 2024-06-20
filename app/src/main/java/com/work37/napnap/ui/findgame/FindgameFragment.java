package com.work37.napnap.ui.findgame;

import static com.work37.napnap.R.*;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.work37.napnap.R;
import com.work37.napnap.databinding.FragmentFindGameBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FindgameFragment extends Fragment {

    private FragmentFindGameBinding binding;
    private Set<String> selectedTags = new HashSet<>();
    private TagFilteredGameListFragment tagFilteredGameListFragment;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFindGameBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        // Initialize RecyclerView with TagFilteredGameListFragment
        if (savedInstanceState == null) {
            tagFilteredGameListFragment = TagFilteredGameListFragment.newInstance(new ArrayList<>(selectedTags));
            getChildFragmentManager().beginTransaction()
                    .replace(id.recyclerView1, tagFilteredGameListFragment)
                    .commit();
        } else {
            tagFilteredGameListFragment = (TagFilteredGameListFragment) getChildFragmentManager().findFragmentById(R.id.recyclerView1);
        }
        setupCategoryButtons();
        return root;
    }

    private void setupCategoryButtons() {
        // Set click listeners for all category buttons
        binding.btnAll.setOnClickListener(v -> {
            selectedTags.clear();
            updateGameList();
            updateButtonStates();
        });
        binding.btnAdventure.setOnClickListener(v -> {
            toggleTag("冒险", binding.btnAdventure);
        });
        binding.btnAnime.setOnClickListener(v -> {
            toggleTag("二次元", binding.btnAnime);
        });
        binding.btnAction.setOnClickListener(v -> {
            toggleTag("动作", binding.btnAction);
        });
        binding.btnCard.setOnClickListener(v -> {
            toggleTag("卡牌", binding.btnCard);
        });
        binding.btnNap.setOnClickListener(v -> {
            toggleTag("休闲", binding.btnNap);
        });
        binding.btnShoot.setOnClickListener(v -> {
            toggleTag("射击", binding.btnShoot);
        });
        binding.btnSingle.setOnClickListener(v -> {
            toggleTag("单机", binding.btnSingle);
        });
        binding.btnTest.setOnClickListener(v -> {
            toggleTag("测试", binding.btnTest);
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
            binding.btnAll.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.selected_button_color));
        } else {
            binding.btnAll.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.default_button_color));
        }
    }

    private void updateGameList() {
        if (tagFilteredGameListFragment != null) {
            tagFilteredGameListFragment.updateTags(new ArrayList<>(selectedTags));
        }
    }
    private void updateButtonStates() {
        // 更新所有按钮的颜色状态
        binding.btnAll.setBackgroundColor(selectedTags.isEmpty() ? ContextCompat.getColor(getContext(), R.color.selected_button_color): ContextCompat.getColor(getContext(), R.color.default_button_color));
        updateButtonState(binding.btnAdventure, "冒险");
        updateButtonState(binding.btnAnime, "二次元");
        updateButtonState(binding.btnAction, "动作");
        updateButtonState(binding.btnCard, "卡牌");
        updateButtonState(binding.btnNap, "休闲");
        updateButtonState(binding.btnShoot, "射击");
        updateButtonState(binding.btnSingle, "单机");
        updateButtonState(binding.btnTest, "测试");
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

