package com.work37.napnap.ui.findgame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.work37.napnap.databinding.FragmentFindgameBinding;

public class FindgameFragment extends Fragment {

    private FragmentFindgameBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FindgameViewModel findgameViewModel =
                new ViewModelProvider(this).get(FindgameViewModel.class);

        binding = FragmentFindgameBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.findGame;
        findgameViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}