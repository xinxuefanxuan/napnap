package com.work37.napnap.detail;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.work37.napnap.Adaptor.GameAdaptor;
import com.work37.napnap.Adaptor.PostAdaptor;
import com.work37.napnap.databinding.ActivityUserDetailBinding;
import com.work37.napnap.entity.Game;
import com.work37.napnap.entity.Post;
import com.work37.napnap.global.PersistentCookieJar;
import com.work37.napnap.global.PublicActivity;
import com.work37.napnap.global.UrlConstant;
import com.work37.napnap.RequestAndResponse.GameResponse;
import com.work37.napnap.RequestAndResponse.PostResponse;
import com.work37.napnap.ui.userlogin_register.User;

import org.json.JSONException;
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
    private TextView userName, userAccount, userProfile, userLikes, userFollows, userFollowers;
    private Button followButton;
    private RecyclerView recyclerView;
    private Button tabPosts, tabApps, tabArticles;
    private User user;
    private boolean isFollowing;

    private boolean hasMoreData = true; // 是否还有更多数据的标志

    private PostAdaptor postAdapter;
    private GameAdaptor gameAdapter;
    private List<Post> postList = new ArrayList<>();
    private List<Game> gameList = new ArrayList<>();
    private List<Post> myPostList = new ArrayList<>();
    private int currentPage = 1;
    private boolean isLoading = false;
    private String currentTab = "posts"; // To track which tab is currently active

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
        userProfile = binding.userProfile;
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
            userProfile.setText(user.getProfile());
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

        //每一个tab点击之后响应事件
        tabPosts.setOnClickListener(v -> {
            currentTab = "posts";
            currentPage = 1;
            hasMoreData = true; // 重置标志
            postList.clear();
            recyclerView.setAdapter(postAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            loadPosts(user);
        });

        tabApps.setOnClickListener(v -> {
            currentTab = "apps";
            currentPage = 1;
            hasMoreData = true; // 重置标志
            gameList.clear();
            recyclerView.setAdapter(gameAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            loadApps(user);
        });

        tabArticles.setOnClickListener(v -> {
            currentTab = "articles";
            currentPage = 1;
            hasMoreData = true; // 重置标志
            myPostList.clear();
            recyclerView.setAdapter(postAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            loadArticles(user);
        });

        // 初始化recycleview
        postAdapter = new PostAdaptor(getApplicationContext(), postList);
        gameAdapter = new GameAdaptor(getApplicationContext(), gameList);
        recyclerView.setAdapter(postAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadPosts(user);  // 初始加载动态的内容

        // Add scroll listener for pagination
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == getCurrentListSize() - 1&&hasMoreData) {
                    currentPage++;
                    loadMoreData();
                    isLoading = true;
                }
            }
        });
    }

    private int getCurrentListSize() {
        switch (currentTab) {
            case "posts":
                return postList.size();
            case "apps":
                return gameList.size();
            case "articles":
                return myPostList.size();
            default:
                return 0;
        }
    }

    private void loadMoreData() {
        switch (currentTab) {
            case "posts":
                loadPosts(user);
                break;
            case "apps":
                loadApps(user);
                break;
            case "articles":
                loadArticles(user);
                break;
        }
    }

    private boolean checkIfFollowing(User user) {
        Long uid = user.getId();
        new Thread(() -> {
            try {
                // Create JSON object
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("followerId", uid);

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
                boolean data = jsonObject.getBoolean("data");
                if (code == 0) {
                    isFollowing = data;
                    updateFollowButton();
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
                jsonObject1.put("followerId", uid);

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
                if (code == 0) {
                    runOnUiThread(() -> {
                        try {
                            JSONObject data = (JSONObject) jsonObject.get("data");
                            long fansNum = data.getLong("fansNum");
                            long focusNum = data.getLong("focusNum");
                            updatecollectAndlikeCount(fansNum,focusNum);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
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
                jsonObject1.put("followerId", uid);

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
                if (code == 0) {
                    runOnUiThread(() -> {
                        try {
                            JSONObject data = (JSONObject) jsonObject.get("data");
                            long fansNum = data.getLong("fansNum");
                            long focusNum = data.getLong("focusNum");
                            updatecollectAndlikeCount(fansNum,focusNum);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
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
                jsonObject1.put("userId", uid);
                jsonObject1.put("page", currentPage);
                jsonObject1.put("pageSize",10);
                jsonObject1.put("sortField","");
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
                Gson gson = new Gson();
                PostResponse postResponse = gson.fromJson(responseBody, PostResponse.class);
                if (code == 0) {
                    List<Post> newPosts = postResponse.getData().getRecords();
                    if(newPosts.size()<10){
                        hasMoreData = false;
                    }
                    runOnUiThread(() -> {
                        postList.addAll(newPosts);
                        currentPage++;
                        postAdapter.notifyDataSetChanged();
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

    private void loadApps(User user) {
        Long uid = user.getId();
        new Thread(() -> {
            try {
                // Create JSON object for request body
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("userId", uid);
                jsonObject1.put("page", currentPage);
                jsonObject1.put("pageSize",10);
                jsonObject1.put("sortField","");

                RequestBody requestBody = RequestBody.create(
                        jsonObject1.toString(),
                        MediaType.get("application/json; charset=utf-8")
                );

                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .cookieJar(new PersistentCookieJar(getApplicationContext()))
                        .build();
                Request request = new Request.Builder()
                        .url(UrlConstant.baseUrl + "api/game/listAllGameByUserCollect")
                        .post(requestBody)
                        .build();
                Response response = okHttpClient.newCall(request).execute();
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                int code = jsonObject.getInt("code");
                Gson gson = new Gson();
                GameResponse gameResponse = gson.fromJson(responseBody, GameResponse.class);
                if (code == 0) {
                    List<Game> newGames = gameResponse.getData().getRecords();
                    if(newGames.size()<10){
                        hasMoreData = false;
                    }
                    runOnUiThread(() -> {
                        currentPage++;
                        gameList.addAll(newGames);
                        gameAdapter.notifyDataSetChanged();
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

    private void loadArticles(User user) {
        Long uid = user.getId();
        new Thread(() -> {
            try {
                // Create JSON object for request body
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("userId", uid);
                jsonObject1.put("current", currentPage);
                jsonObject1.put("pageSize",10);
                jsonObject1.put("sortField","");
                RequestBody requestBody = RequestBody.create(
                        jsonObject1.toString(),
                        MediaType.get("application/json; charset=utf-8")
                );

                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .cookieJar(new PersistentCookieJar(getApplicationContext()))
                        .build();
                Request request = new Request.Builder()
                        .url(UrlConstant.baseUrl + "api/post/listAllPostByUserCollect")
                        .post(requestBody)
                        .build();
                Response response = okHttpClient.newCall(request).execute();
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                int code = jsonObject.getInt("code");
                Gson gson = new Gson();
                PostResponse postResponse = gson.fromJson(responseBody, PostResponse.class);
                if (code == 0) {
                    List<Post> newArticles = postResponse.getData().getRecords();
                    if(newArticles.size()<10){
                        hasMoreData = false;
                    }
                    runOnUiThread(() -> {
                        currentPage++;
                        myPostList.addAll(newArticles);
                        postAdapter.notifyDataSetChanged();
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
    private void updatecollectAndlikeCount(Long fansNum,Long focusNum){
        userFollowers.setText("粉丝:"+fansNum);
        userFollows.setText("关注:"+focusNum);
    }
}



