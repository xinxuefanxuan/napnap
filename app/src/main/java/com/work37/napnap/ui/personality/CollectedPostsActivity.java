package com.work37.napnap.ui.personality;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.work37.napnap.Adaptor.PostAdaptor;
import com.work37.napnap.R;
import com.work37.napnap.entity.Post;
import com.work37.napnap.global.PublicActivity;
import com.work37.napnap.global.UrlConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CollectedPostsActivity extends PublicActivity {

//    private RecyclerView recyclerView;
//    private PostAdaptor postAdaptor;
//    private ProgressBar progressBar;
//    private TextView errorTextView;
//
//    private ImageButton backbutton;
//    private boolean isLoading = false;
//    private int currentPage = 1;
//    private int pageSize = 10;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_collected_posts);
//
//        recyclerView = findViewById(R.id.recycler_view);
//        progressBar = findViewById(R.id.progress_bar);
//        errorTextView = findViewById(R.id.error_text_view);
//        backbutton = findViewById(R.id.backButton);
//
//        postAdaptor = new PostAdaptor(new ArrayList<Post>(), this::openPostDetail);
//        recyclerView.setAdapter(postAdaptor);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        fetchPosts(currentPage, pageSize);
//
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (!recyclerView.canScrollVertically(1) && !isLoading) {
//                    loadMorePosts();
//                }
//            }
//        });
//
//        backbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//    }
//
//    private void loadMorePosts() {
//        isLoading = true;
//        currentPage++;
//        fetchPosts(currentPage, pageSize);
//    }
//
//    private void fetchPosts(int currentPage, int pageSize) {
//        showLoading();
//        JSONObject json = new JSONObject();
//        try {
//            json.put("current", currentPage);
//            json.put("pagesize", pageSize);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        new Thread(() -> {
//            OkHttpClient client = new OkHttpClient();
//            RequestBody requestBody = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
//            Request request = new Request.Builder()
//                    .url(UrlConstant.baseUrl + "/api/user/collectedPosts")
//                    .post(requestBody)
//                    .build();
//
//            try {
//                Response response = client.newCall(request).execute();
//                String responseBody = response.body().string();
//                JSONObject jsonObject = new JSONObject(responseBody);
//                int code = jsonObject.getInt("code");
//                String message = jsonObject.getString("message");
//                if (code == 0) {
//                    JSONArray data = jsonObject.getJSONArray("data");
//                    List<Post> posts = parsePosts(data);
//                    runOnUiThread(() -> {
//                        updateUI(posts);
//                        hideLoading();
//                    });
//                } else {
//                    runOnUiThread(() -> {
//                        showError(message);
//                        hideLoading();
//                    });
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                runOnUiThread(() -> {
//                    showError("请求失败");
//                    hideLoading();
//                });
//            }
//        }).start();
//    }
//
//    private List<Post> parsePosts(JSONArray data) throws JSONException {
//        List<Post> posts = new ArrayList<>();
//        for (int i = 0; i < data.length(); i++) {
//            JSONObject jsonPost = data.getJSONObject(i);
//            Post post = new Post();
//            post.setUserId((int)jsonPost.getLong("userId"));
//            post.setTitle(jsonPost.getString("title"));
//            post.setContent(jsonPost.getString("content"));
//            post.setPicture(parsePictures(jsonPost.getJSONArray("pictures")));
//            post.setTag(parseTags(jsonPost.getJSONArray("tag")));
//            post.setLikes((int)jsonPost.getLong("likes"));
//            post.setCollectNum((int)jsonPost.getLong("collectNum"));
//            posts.add(post);
//        }
//        return posts;
//    }
//
//    private List<String> parsePictures(JSONArray jsonArray) throws JSONException {
//        List<String> list = new ArrayList<>();
//        for (int i = 0; i < jsonArray.length(); i++) {
//            list.add(jsonArray.getString(i));
//        }
//        return list;
//    }
//
//    private List<String> parseTags(JSONArray jsonArray) throws JSONException {
//        List<String> list = new ArrayList<>();
//        for (int i = 0; i < jsonArray.length(); i++) {
//            list.add(jsonArray.getString(i));
//        }
//        return list;
//    }
//
//    private void showLoading() {
//        progressBar.setVisibility(View.VISIBLE);
//        errorTextView.setVisibility(View.GONE);
//    }
//
//    private void hideLoading() {
//        progressBar.setVisibility(View.GONE);
//    }
//
//    private void showError(String message) {
//        errorTextView.setText(message);
//        errorTextView.setVisibility(View.VISIBLE);
//    }

//    private void updateUI(List<Post> newPosts) {
//        postAdaptor.addPosts(newPosts);
//        isLoading = false;
//    }
//
//    private void openPostDetail(Post post) {
//        Intent intent = new Intent(this, PostDetailActivity.class);
//        intent.putExtra("post", (CharSequence) post);
//        startActivity(intent);
//    }
}

