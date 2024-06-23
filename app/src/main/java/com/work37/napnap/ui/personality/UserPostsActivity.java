package com.work37.napnap.ui.personality;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.work37.napnap.Adaptor.PostAdaptor;
import com.work37.napnap.Game.PostRequest;
import com.work37.napnap.R;
import com.work37.napnap.entity.Post;
import com.work37.napnap.global.PersistentCookieJar;
import com.work37.napnap.global.PublicActivity;
import com.work37.napnap.global.UrlConstant;
import com.work37.napnap.ui.search.PostResponse;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserPostsActivity extends PublicActivity {
    private RecyclerView recyclerView;
    private PostAdaptor postAdaptor;
    private List<Post> collectedPostList;
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
        collectedPostList = new ArrayList<>();
        postAdaptor = new PostAdaptor(this, collectedPostList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postAdaptor);

        // Add scroll listener to RecyclerView for pagination
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == collectedPostList.size() - 1 && !isLoading && !isLastPage) {
                    currentPage++;
                    try {
                        fetchCollectedGames();
                    } catch (IOException | JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        // Fetch initial data
        try {
            fetchCollectedGames();
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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
        collectedPostList.clear();
        postAdaptor.notifyDataSetChanged();
        try {
            fetchCollectedGames();
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void fetchCollectedGames() throws IOException, JSONException {
        isLoading = true;

        new Thread(() -> {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cookieJar(new PersistentCookieJar(getApplicationContext()))
                    .build();

            PostRequest postRequest = new PostRequest();
            postRequest .setCurrent(currentPage);
            postRequest .setPageSize(pageSize);
            postRequest .setSortField("");
            Gson gson = new Gson();
            String json = gson.toJson(postRequest);

            RequestBody requestBody = RequestBody.create(
                    json,
                    MediaType.get("application/json; charset=utf-8")
            );
            Request request = new Request.Builder()
                    .url(UrlConstant.baseUrl + "api/post/listAllPostByUser")
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

            PostResponse postResponse = gson.fromJson(responseBody, PostResponse.class);

            if (postResponse.getCode() == 0) {
                List<Post> records = postResponse.getData().getRecords();

                new Handler(Looper.getMainLooper()).post(() -> {
                    collectedPostList.addAll(records);
                    postAdaptor.notifyDataSetChanged();
                    isLoading = false;
                    if (records.size() < pageSize) {
                        isLastPage = true;
                    } else {
                        isLoading = false;
                    }
                });
            }
        }).start();
    }
}
