package com.work37.napnap.detail;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.work37.napnap.Adaptor.GameAdaptor;
import com.work37.napnap.Adaptor.PostAdaptor;
import com.work37.napnap.R;
import com.work37.napnap.databinding.ActivityUserDetailBinding;
import com.work37.napnap.entity.Game;
import com.work37.napnap.entity.Post;
import com.work37.napnap.global.PersistentCookieJar;
import com.work37.napnap.global.PublicActivity;
import com.work37.napnap.global.PublicApplication;
import com.work37.napnap.global.UrlConstant;
import com.work37.napnap.ui.userlogin_register.LoginReponse;
import com.work37.napnap.ui.userlogin_register.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserDetailActivity extends PublicActivity {

    private ActivityUserDetailBinding binding;
    private ImageView userAvatar;
    private TextView userName, userAccount, userLikes, userFollows, userFollowers;
    private Button followButton;
    private RecyclerView recyclerView;
    private Button tabPosts, tabApps, tabArticles;
    private User user;
    private boolean isFollowing;

    private PostAdaptor postAdapter;
    private GameAdaptor gameAdaptor;
    private List<Post> postList = new ArrayList<>();
    private List<Game> gamelist = new ArrayList<>();
    private List<Post> myPostList = new ArrayList<>();
    private int currentPage = 1;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 初始化组件
        userAvatar = binding.userAvatar;
        userName = binding.userName;
        userAccount = binding.userAccount;
        userFollows = binding.userFollows;
        userFollowers = binding.userFollowers;
        followButton = binding.followButton;
        recyclerView = binding.recyclerView;
        tabPosts = binding.tabPosts;
        tabApps = binding.tabApps;
        tabArticles = binding.tabArticles;

        // Get user data from intent
        user = (User) getIntent().getSerializableExtra("User");

        // Load user data
        if (user != null) {
            Glide.with(this).load(user.getUserAvatar()).into(userAvatar);
            userName.setText(user.getUserName());
            userAccount.setText(user.getUserAccount());
            userFollows.setText("关注: " + user.getFollowNum());
            userFollowers.setText("粉丝: " + user.getFanNum());
            // 检查是否已经关注
            checkIfFollowing(user);
            updateFollowButton();
        }

        // 监听关注按钮的事件
        followButton.setOnClickListener(v -> {
            if (isFollowing) {
                // Unfollow user
                unfollowUser(user);
            } else {
                // Follow user
                followUser(user);
            }
        });

        tabPosts.setOnClickListener(v -> {
            currentPage = 1;
            postList.clear();
            recyclerView.setAdapter(postAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            loadPosts(user);
        });

        tabApps.setOnClickListener(v -> {
           currentPage = 1;
           gamelist.clear();
           recyclerView.setAdapter(gameAdaptor);
        });

        tabArticles.setOnClickListener(v -> {
            currentPage = 1;
            myPostList.clear();
            recyclerView.setAdapter(postAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            loadArticles();
        });

        // Initialize RecyclerView
        postAdapter = new PostAdaptor(getApplicationContext(),postList);
        recyclerView.setAdapter(postAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add scroll listener for pagination
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == postList.size() - 1) {
                    // Load more data when reaching the end of the list
                    loadPosts(user);
                    isLoading = true;
                }
            }
        });

        // Load initial posts
        loadPosts(user);
    }

    private boolean checkIfFollowing(User user) {
        Long uid = user.getId();
        new Thread(()->{
        try {
            // Create JSON object
            JSONObject jsonObject1 = new JSONObject();
            try {
                jsonObject1.put("followerId", uid);
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
                    .url(UrlConstant.baseUrl + "api/follower/isFollow")
                    .post(requestBody)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            String responseBody = response.body().string();
            JSONObject jsonObject = new JSONObject(responseBody);
            int code = jsonObject.getInt("code");
            String message = jsonObject.getString("message");
            boolean data = jsonObject.getBoolean("data");
            if (code == 0) {
                runOnUiThread(() -> {
                    if(data==true){
                        isFollowing = true;
                    }else{
                        isFollowing = false;
                    }
                    updateFollowButton();
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_SHORT).show();
                });
            }
        } catch (Exception e) {
            Log.d("bbb", "响应失败");
            e.printStackTrace();
            runOnUiThread(() -> {
                Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_SHORT).show();
            });
        }

        }).start();
        return true;
    }

    private void updateFollowButton() {
        followButton.setText(isFollowing ? "已关注" : "关注");
    }

    private void followUser(User user) {
        Long uid = user.getId();
        // 点击按钮关注用户
        new Thread(() -> {
            try {
                // Create JSON object
                JSONObject jsonObject1 = new JSONObject();
                try {
                    jsonObject1.put("followerId", uid);
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
                        .url(UrlConstant.baseUrl + "api/follower/addFocus")
                        .post(requestBody)
                        .build();
                Response response = okHttpClient.newCall(request).execute();
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                int code = jsonObject.getInt("code");
                String message = jsonObject.getString("message");
                if (code == 0) {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "关注成功", Toast.LENGTH_SHORT).show();
                        isFollowing = true;
                        updateFollowButton();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "关注失败", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                Log.d("bbb", "响应失败");
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "响应失败", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void unfollowUser(User user) {
        Long uid = user.getId();
        // 点击按钮关注用户
        new Thread(() -> {
            try {
                // Create JSON object
                JSONObject jsonObject1 = new JSONObject();
                try {
                    jsonObject1.put("followerId", uid);
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
                        .url(UrlConstant.baseUrl + "api/follower/removeFocus")
                        .post(requestBody)
                        .build();
                Response response = okHttpClient.newCall(request).execute();
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                int code = jsonObject.getInt("code");
                String message = jsonObject.getString("message");
                if (code == 0) {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "取消关注成功", Toast.LENGTH_SHORT).show();
                        isFollowing = false;
                        updateFollowButton();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "取消关注失败", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                Log.d("bbb", "响应失败");
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "响应失败", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void loadPosts(User user) {
        Long uid = user.getId();
        new Thread(() -> {
            try {
                // Create JSON object for request body
                JSONObject jsonObject1 = new JSONObject();
                try {
                    jsonObject1.put("userId", uid);
                    jsonObject1.put("page", currentPage);
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
                        .url(UrlConstant.baseUrl + "api/post/listAllPostByUser")
                        .post(requestBody)
                        .build();
                Response response = okHttpClient.newCall(request).execute();
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                int code = jsonObject.getInt("code");
                String message = jsonObject.getString("message");
                if (code == 0) {
                    JSONArray postsArray = jsonObject.getJSONArray("data");
                    List<Post> newPosts = new ArrayList<>();
                    for (int i = 0; i < postsArray.length(); i++) {
                        JSONObject postObject = postsArray.getJSONObject(i);
                        Post post = new Post();
                        post.setTitle(postObject.getString("title"));
                        post.setContent(postObject.getString("content"));
                        // Add other necessary fields
                        newPosts.add(post);
                    }
                    runOnUiThread(() -> {
                        postList.addAll(newPosts);
                        currentPage++;
                        isLoading = false;
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                Log.d("bbb", "响应失败");
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "响应失败", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void loadApps() {
        // Similar implementation for loading user's favorite apps
    }

    private void loadArticles() {
        // Similar implementation for loading user's favorite articles
    }
}


