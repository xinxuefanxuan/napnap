package com.work37.napnap;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.work37.napnap.entity.Game;
import com.work37.napnap.global.PublicActivity;

public class GameDetailActivity extends PublicActivity {
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);

        ImageView gameIcon = findViewById(R.id.gameIcon);
        TextView gameName = findViewById(R.id.gameName);
        TextView gameType = findViewById(R.id.gameType);
        TextView gameScore = findViewById(R.id.gameScore);
        TextView gameFavorites = findViewById(R.id.gameFavorites);
        TextView gameComments = findViewById(R.id.gameComments);
        TextView gameDescription = findViewById(R.id.gameDescription);
        TextView gameSize = findViewById(R.id.gameSize);

        // Get the game object passed from the adapter
        Game game = (Game) getIntent().getSerializableExtra("Game");

        if (game != null) {
            // Load game icon
            Glide.with(this).load(game.getGameIcon()).into(gameIcon);

            // Set game details
            gameName.setText(game.getGameName());
            gameType.setText("Type: " + game.getTag().toString());
            gameScore.setText("Score: " + game.getGameScore());
            gameFavorites.setText("Favorites: " + game.getCollectNum());
            gameComments.setText("Comments:"+game.getCollectNum());
            gameDescription.setText(game.getGameProfile());
            gameSize.setText("Size: " + game.getGameSize());
        }
    }
}
