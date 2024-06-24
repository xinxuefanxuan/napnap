package com.work37.napnap.Adaptor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.work37.napnap.GameDetailActivity;
import com.work37.napnap.R;
import com.work37.napnap.entity.Game;

import java.util.List;

public class GameAdaptor extends RecyclerView.Adapter<GameAdaptor.ViewHolder> {
    private Context context;
    private List<Game> gameList;

    public GameAdaptor(Context context, List<Game> gameList) {
        this.context = context;
        this.gameList = gameList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_app, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Game game = gameList.get(position);

        // Set game logo
        if (!game.getGameIcon().isEmpty()) {
            Glide.with(context)
                    .load(game.getGameIcon())
                    .into(holder.appIcon);
        }

        // Set game name with maximum 10 characters, exceeding parts are replaced with ...
        String gameName = game.getGameName();
        if (gameName.length() > 10) {
            gameName = gameName.substring(0, 10) + "...";
        }
        holder.appName.setText(gameName);


        // Set game tags (assuming it's a comma-separated string)
        List<String> tags = game.getTag(); // 假设返回的是字符串数组 ["冒险", "二次元"]
        String formattedTags = formatTags(tags); // 调用自定义方法格式化标签
        holder.appTags.setText(formattedTags);

        // Set game score
        holder.appScore.setText(String.valueOf(game.getGameScore()));

        // Set click listener for check button
        holder.checkButton.setOnClickListener(v -> {
            // Implement download functionality here if needed
            Intent intent = new Intent(context, GameDetailActivity.class);
            intent.putExtra("Game", game);
            Log.d("aaabbbccc", game.toString());
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        });
    }

    private String formatTags(List<String> tags) {
        StringBuilder sb = new StringBuilder();
        for (String tag : tags) {
            sb.append("#").append(tag).append("  "); // 添加 # 前缀和空格
        }
        return sb.toString().trim(); // 去除末尾多余的空格
    }
    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView appTags;
        TextView appScore;
        Button checkButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.appIcon);
            appName = itemView.findViewById(R.id.appName);
            appTags = itemView.findViewById(R.id.appTags);
            appScore = itemView.findViewById(R.id.appScore);
            checkButton = itemView.findViewById(R.id.downloadButton);
        }
    }
}
