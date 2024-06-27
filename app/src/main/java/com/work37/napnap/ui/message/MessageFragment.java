package com.work37.napnap.ui.message;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.work37.napnap.R;
import com.work37.napnap.databinding.FragmentMessageBinding;
public class MessageFragment extends Fragment {
    private FragmentMessageBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MessageViewModel messageViewModel =
                new ViewModelProvider(this).get(MessageViewModel.class);

        binding = FragmentMessageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button newFansButton = root.findViewById(R.id.newFansButton);
        newFansButton.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), ActivityMessageGeneral.class);
            intent.putExtra("messageType", "NewFans");
            startActivity(intent);
        });

        Button newLikeButton = root.findViewById(R.id.newLikeButton);
        newLikeButton.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), ActivityMessageGeneral.class);
            intent.putExtra("messageType", "NewLike");
            startActivity(intent);
        });

        Button newFavButton = root.findViewById(R.id.newFavButton);
        newFavButton.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), ActivityMessageGeneral.class);
            intent.putExtra("messageType", "NewFav");
            startActivity(intent);
        });

        Button newReplyButton = root.findViewById(R.id.newReplyButton);
        newReplyButton.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), ActivityMessageGeneral.class);
            intent.putExtra("messageType", "NewReply");
            startActivity(intent);
        });



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

