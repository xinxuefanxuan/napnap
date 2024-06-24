package com.work37.napnap.ui.findgame;

import static com.work37.napnap.R.*;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.work37.napnap.R;
import com.work37.napnap.databinding.FragmentFindGameBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FragmentFindGame extends Fragment {

    private FragmentFindGameBinding binding;
    private Set<String> selectedTags = new HashSet<>();
    private FragmentFindGameList fragmentFindGameList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFindGameBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        // Initialize RecyclerView with TagFilteredGameListFragment
        if (savedInstanceState == null) {
            fragmentFindGameList = FragmentFindGameList.newInstance(new ArrayList<>(selectedTags));
            getChildFragmentManager().beginTransaction()
                    .replace(id.findgamePager, fragmentFindGameList)
                    .commit();
        } else {
            fragmentFindGameList = (FragmentFindGameList) getChildFragmentManager().findFragmentById(R.id.findgamePager);
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
            toggleTag("冒险");
        });
        binding.btnAnime.setOnClickListener(v -> {
            toggleTag("二次元");
        });
        binding.btnAction.setOnClickListener(v -> {
            toggleTag("动作");
        });
        binding.btnCard.setOnClickListener(v -> {
            toggleTag("卡牌");
        });
        binding.btnNap.setOnClickListener(v -> {
            toggleTag("休闲");
        });
        binding.btnShoot.setOnClickListener(v -> {
            toggleTag("射击");
        });
        binding.btnSingle.setOnClickListener(v -> {
            toggleTag("单机");
        });
        binding.btnTest.setOnClickListener(v -> {
            toggleTag("测试");
        });
    }

//    @SuppressLint("ResourceAsColor")
    @SuppressLint("ResourceAsColor")
    private void toggleTag(String tag) {
        if (selectedTags.contains(tag)) {
            selectedTags.remove(tag);
        } else {
            selectedTags.add(tag);
        }
        updateGameList();
        updateButtonStates();
    }

    private void updateGameList() {
        if (fragmentFindGameList != null) {
            fragmentFindGameList.updateTags(new ArrayList<>(selectedTags));
        }
    }
    private void updateButtonStates() {
        // 更新所有按钮的颜色状态
        binding.btnAll.setBackgroundResource(selectedTags.isEmpty() ?
                drawable.category_button_selected_background :
                drawable.category_button_unselected_background );
        binding.btnAll.setTextColor(selectedTags.isEmpty() ?
                ContextCompat.getColor(getContext(), R.color.category_button_selectedTextColor) :
                ContextCompat.getColor(getContext(), R.color.category_button_unselectedTextColor));
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
            button.setBackgroundResource(R.drawable.category_button_selected_background);
            button.setTextColor(ContextCompat.getColor(getContext(), R.color.category_button_selectedTextColor));
        } else {
            button.setBackgroundResource(R.drawable.category_button_unselected_background);
            button.setTextColor(ContextCompat.getColor(getContext(), R.color.category_button_unselectedTextColor));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

