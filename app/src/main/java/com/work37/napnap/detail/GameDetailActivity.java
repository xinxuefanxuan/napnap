package com.work37.napnap.detail;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.work37.napnap.R;
import com.work37.napnap.entity.Game;
import com.work37.napnap.global.PersistentCookieJar;
import com.work37.napnap.global.PublicActivity;
import com.work37.napnap.global.UrlConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GameDetailActivity extends PublicActivity {
    private boolean collected = false;
    private TextView gameScore;
    private TextView gameFavorites;
    private Button likeButton;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);

        ImageView gameIcon = findViewById(R.id.gameIcon);
        TextView gameName = findViewById(R.id.gameName);
        TextView gameType = findViewById(R.id.gameType);
        gameScore = findViewById(R.id.gameScore);
        gameFavorites = findViewById(R.id.gameFavorites);
        TextView gameDescription = findViewById(R.id.gameDescription);
        Button download = findViewById(R.id.downloadButton);
        ImageButton back = findViewById(R.id.backButton);
        Button rank = findViewById(R.id.rateButton);
        likeButton = findViewById(R.id.likeButton);

        Game game = (Game) getIntent().getSerializableExtra("Game");

        isCollected(game); // 判断是否收藏
        updateLikeButton();

        back.setOnClickListener(v -> finish());

        // 打分按钮
        rank.setOnClickListener(v -> enterscoreDialog(game));

        // 收藏按钮
        likeButton.setOnClickListener(v -> collectGame(game));

        if (game != null) {
            // Load game icon
            Glide.with(this).load(game.getGameIcon()).into(gameIcon);

            // Set game details
            gameName.setText(game.getGameName());
            List<String> tags = game.getTag(); // 假设返回的是字符串数组 ["冒险", "二次元"]
            String formattedTags = formatTags(tags); // 调用自定义方法格式化标签
            gameType.setText("类型: " + formattedTags);
            gameScore.setText("评分: " + game.getGameScore());
            gameFavorites.setText("收藏: " + game.getCollectNum());
            gameDescription.setText(game.getGameProfile());
            download.setText(game.getGameSize() + "KB");
        }
    }

    private void updateLikeButton() {
        if (collected) {
            likeButton.setText("取消收藏");
        } else {
            likeButton.setText("收藏");
        }
    }

    private String formatTags(List<String> tags) {
        StringBuilder sb = new StringBuilder();
        for (String tag : tags) {
            sb.append("#").append(tag).append("  "); // 添加 # 前缀和空格
        }
        return sb.toString().trim(); // 去除末尾多余的空格
    }

    // 显示打分视图
    private void enterscoreDialog(Game game) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_enter_score, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        Button confirmButton = dialogView.findViewById(R.id.confirmButton);

        AlertDialog dialog = builder.create();

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        confirmButton.setOnClickListener(v -> new Thread(() -> {
            if (ratingBar.getRating() >= 0) {
                float trueScore = ratingBar.getRating();
                JSONObject jsonObject1 = new JSONObject();
                try {
                    jsonObject1.put("gameId", game.getId());
                    jsonObject1.put("score", trueScore);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                RequestBody requestBody = RequestBody.create(
                        jsonObject1.toString(),
                        MediaType.get("application/json; charset=utf-8")
                );
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .cookieJar(new PersistentCookieJar(getApplicationContext()))
                        .build();
                Request request = new Request.Builder()
                        .url(UrlConstant.baseUrl + "api/game/scoreGame")
                        .post(requestBody)
                        .build();

                try {
                    Response response = okHttpClient.newCall(request).execute();
                    String responseBody = response.body().string();
                    Log.d("aaa", responseBody);
                    JSONObject jsonObject = new JSONObject(responseBody);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        JSONObject finalJsonObject = jsonObject;
                        runOnUiThread(() -> {
                            try {
                                JSONObject data = finalJsonObject.getJSONObject("data");
                                float gamescore = (float) data.getDouble("gameScore");
                                long gamecollect = data.getLong("collectNum");
                                gameScore.setText("评分: " + gamescore);
                                gameFavorites.setText("收藏: " + gamecollect);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                            Toast.makeText(this, "输入的分数: " + trueScore, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        });
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(this, "请求失败", Toast.LENGTH_SHORT).show());
                }
            } else {
                runOnUiThread(() -> Toast.makeText(this, "请输入分数", Toast.LENGTH_SHORT).show());
            }
        }).start());

        dialog.show();
    }

    // 收藏游戏
    private void collectGame(Game game) {
        new Thread(() -> {
            JSONObject jsonObject1 = new JSONObject();
            try {
                jsonObject1.put("collectId", game.getId());
                jsonObject1.put("type", 1);
            } catch (Exception e) {
                e.printStackTrace();
            }

            RequestBody requestBody = RequestBody.create(
                    jsonObject1.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cookieJar(new PersistentCookieJar(getApplicationContext()))
                    .build();
            Request request = new Request.Builder()
                    .url(UrlConstant.baseUrl + "api/game/collectGame")
                    .post(requestBody)
                    .build();

            try {
                Response response = okHttpClient.newCall(request).execute();
                String responseBody = response.body().string();
                Log.d("aaa", responseBody);
                JSONObject jsonObject = new JSONObject(responseBody);
                int code = jsonObject.getInt("code");
                if (code == 0) {
                    JSONObject finalJsonObject = jsonObject;
                    runOnUiThread(() -> {
                        JSONObject data = null;
                        try {
                            data = finalJsonObject.getJSONObject("data");
                            float gamescore = (float) data.getDouble("gameScore");
                            long gamecollect = data.getLong("collectNum");
                            gameScore.setText("评分: " + gamescore);
                            gameFavorites.setText("收藏: " + gamecollect);
                            if (collected) {
                                Toast.makeText(this, "取消收藏成功", Toast.LENGTH_SHORT).show();
                                collected = false;
                            } else {
                                Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
                                collected = true;
                            }
                            updateLikeButton();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "请求失败", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    /**
     * 是否收藏
     * @param game
     */
    private void isCollected(Game game) {
        new Thread(() -> {
            JSONObject jsonObject1 = new JSONObject();
            try {
                jsonObject1.put("collectId", game.getId());
                jsonObject1.put("type", 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            RequestBody requestBody = RequestBody.create(
                    jsonObject1.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cookieJar(new PersistentCookieJar(getApplicationContext()))
                    .build();
            Request request = new Request.Builder()
                    .url(UrlConstant.baseUrl + "api/game/getCollectGameStatus")
                    .post(requestBody)
                    .build();

            try {
                Response response = okHttpClient.newCall(request).execute();
                String responseBody = response.body().string();
                Log.d("aaa", responseBody);
                JSONObject jsonObject = new JSONObject(responseBody);
                int code = jsonObject.getInt("code");
                if (code == 0) {
                    JSONObject finalJsonObject = jsonObject;
                    runOnUiThread(() -> {
                        try {
                            collected = finalJsonObject.getBoolean("data");
                            updateLikeButton();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "请求失败", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}

