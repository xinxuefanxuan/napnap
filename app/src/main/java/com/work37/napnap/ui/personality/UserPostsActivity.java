package com.work37.napnap.ui.personality;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.work37.napnap.Adaptor.MyPostAdaptor;
import com.work37.napnap.Adaptor.PostAdaptor;
import com.work37.napnap.RequestAndResponse.PostRequest;
import com.work37.napnap.R;
import com.work37.napnap.entity.Post;
import com.work37.napnap.global.PersistentCookieJar;
import com.work37.napnap.global.PublicActivity;
import com.work37.napnap.global.PublicApplication;
import com.work37.napnap.global.UrlConstant;
import com.work37.napnap.RequestAndResponse.PostResponse;

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
    private MyPostAdaptor postAdaptor;
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
        postAdaptor = new MyPostAdaptor(this, collectedPostList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postAdaptor);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    int position = viewHolder.getAdapterPosition();
                    MyPostAdaptor.ViewHolder holder = (MyPostAdaptor.ViewHolder) viewHolder;
                    holder.deleteButton.setVisibility(View.VISIBLE);
                    holder.cancelButton.setVisibility(View.VISIBLE);
                    holder.contentLayout.setVisibility(View.GONE);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

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
            postRequest.setUserId(PublicApplication.getCurrentUser().getId());

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
