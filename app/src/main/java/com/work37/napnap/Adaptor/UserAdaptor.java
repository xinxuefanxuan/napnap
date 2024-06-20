package com.work37.napnap.Adaptor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.work37.napnap.R;
import com.work37.napnap.detail.UserProfileActivity;
import com.work37.napnap.ui.userlogin_register.User;

import java.util.List;

public class UserAdaptor extends RecyclerView.Adapter<UserAdaptor.ViewHolder> {
    private Context context;
    private List<User> userList;

    public UserAdaptor(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);

        // Load user avatar (assuming you have a URL or resource)
        Glide.with(context)
                .load(user.getUserAvatar())
                .into(holder.userAvatar);

        // Set user name, followers, and following
        holder.userName.setText(user.getUsername());
        holder.userId.setText(String.valueOf(user.getUid()));
        holder.userFollowers.setText("粉丝: " + user.getFanNum());
        holder.userFollowing.setText("关注: " + user.getFollowNum());

        // Set click listener for view button
        holder.viewButton.setOnClickListener(v -> {
            // Implement view functionality here, e.g., navigate to user profile
            Intent intent = new Intent(context, UserProfileActivity.class);
            intent.putExtra("User", user);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatar;
        TextView userName;
        TextView userId;
        TextView userFollowers;
        TextView userFollowing;
        Button viewButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            userName = itemView.findViewById(R.id.userName);
            userId = itemView.findViewById(R.id.userId);
            userFollowers = itemView.findViewById(R.id.userFollowers);
            userFollowing = itemView.findViewById(R.id.userFollowing);
            viewButton = itemView.findViewById(R.id.viewButton);
        }
    }
}


