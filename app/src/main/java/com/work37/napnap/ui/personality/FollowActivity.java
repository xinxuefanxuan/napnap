package com.work37.napnap.ui.personality;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.work37.napnap.Adaptor.UserAdaptor;
import com.work37.napnap.R;
import com.work37.napnap.global.PersistentCookieJar;
import com.work37.napnap.global.PublicActivity;
import com.work37.napnap.global.UrlConstant;
import com.work37.napnap.ui.search.UserResponse;
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

public class FollowActivity extends PublicActivity {
    private RecyclerView recyclerView;
    private UserAdaptor userAdaptor;
    private List<User> collectedUserList;
    private boolean isLoading = false;
    private int currentPage = 1;
    private int pageSize = 10;
    private boolean isLastPage = false;
    private boolean isFirstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collected_posts);

        recyclerView = findViewById(R.id.recycler_view);
        ImageButton backButton = findViewById(R.id.backButton);

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
                        .url(UrlConstant.baseUrl + "api/user/listUserFocus")
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
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }).start();
    }
}
