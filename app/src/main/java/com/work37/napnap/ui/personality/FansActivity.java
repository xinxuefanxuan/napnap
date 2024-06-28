package com.work37.napnap.ui.personality;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.work37.napnap.Adaptor.UserAdaptor;
import com.work37.napnap.R;
import com.work37.napnap.databinding.ActivityFansBinding;
import com.work37.napnap.global.PersistentCookieJar;
import com.work37.napnap.global.PublicActivity;
import com.work37.napnap.global.UrlConstant;
import com.work37.napnap.RequestAndResponse.UserResponse;
import com.work37.napnap.ui.userlogin_register.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FansActivity extends PublicActivity {
    private ActivityFansBinding activityFansBinding;
    private RecyclerView recyclerView;
    private UserAdaptor userAdaptor;
    private ImageButton backButton;
    private List<User> collectedUserList;
    private ImageView emptyView;
    private boolean isLoading = false;
    private int currentPage = 1;
    private int pageSize = 10;
    private boolean isLastPage = false;
    private boolean isFirstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityFansBinding = ActivityFansBinding.inflate(getLayoutInflater());

        emptyView = activityFansBinding.emptyView;
        recyclerView = activityFansBinding.recyclerView;
        backButton = activityFansBinding.backButton;

        setContentView(activityFansBinding.getRoot());
        // Set up back button
        backButton.setOnClickListener(v -> finish());

        // Initialize game list and adapter
        collectedUserList = new ArrayList<>();
        userAdaptor = new UserAdaptor(this, collectedUserList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdaptor);

        // Add scroll listener to RecyclerView for pagination
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == collectedUserList.size() - 1 && !isLoading && !isLastPage) {
                    currentPage++;
                    try {
                        fetchCollectedPosts();
                    } catch (IOException | JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        // Fetch initial data
        try {
            fetchCollectedPosts();
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFirstLoad) {
            refreshPostList();
        } else {
            isFirstLoad = false;
        }
    }

    private void refreshPostList() {
        currentPage = 1;
        isLastPage = false;
        collectedUserList.clear();
        userAdaptor.notifyDataSetChanged();
        try {
            fetchCollectedPosts();
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void fetchCollectedPosts() throws IOException, JSONException {
        isLoading = true;

        new Thread(() -> {
            try {
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .cookieJar(new PersistentCookieJar(getApplicationContext()))
                        .build();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("current",currentPage);
                jsonObject.put("pageSize",10);
                jsonObject.put("sortField","");

                Gson gson = new Gson();

                RequestBody requestBody = RequestBody.create(
                        jsonObject.toString(),
                        MediaType.get("application/json; charset=utf-8")
                );
                Request request = new Request.Builder()
                        .url(UrlConstant.baseUrl + "api/user/listUserFollowers")
                        .post(requestBody)
                        .build();
                Response response;
                String responseBody;
                try {
                    response = okHttpClient.newCall(request).execute();
                    responseBody = response.body().string();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                UserResponse userResponse = gson.fromJson(responseBody, UserResponse.class);

                if (userResponse.getCode() == 0) {
                    List<User> records = userResponse.getData().getRecords();

                    new Handler(Looper.getMainLooper()).post(() -> {
                        collectedUserList.addAll(records);
                        userAdaptor.notifyDataSetChanged();
                        isLoading = false;
                        if (records.size() < pageSize) {
                            isLastPage = true;
                        } else {
                            isLoading = false;
                        }

                        // 控制空视图的可见性
                        if (collectedUserList==null|| collectedUserList.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                        }

                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }).start();
    }
}
