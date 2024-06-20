package com.work37.napnap.ui.search;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.work37.napnap.Adaptor.PostAdaptor;
import com.work37.napnap.Adaptor.UserAdaptor;
import com.work37.napnap.R;
import com.work37.napnap.entity.Post;
import com.work37.napnap.global.PersistentCookieJar;
import com.work37.napnap.global.UrlConstant;
import com.work37.napnap.ui.userlogin_register.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class PostListFragment extends Fragment {
        private RecyclerView recyclerView;
        private PostAdaptor postAdaptor;
        private List<Post> postList;
        private boolean isLoading = false;
        private int currentPage = 1;
        private int pageSize = 10;
        private boolean isLastPage = false;
        private boolean isFirstLoad = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        postList = new ArrayList<>();
        postAdaptor = new PostAdaptor(getContext(), postList);
        recyclerView.setAdapter(postAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == postList.size() - 1 && !isLoading && !isLastPage) {
                    currentPage++;
                    fetchPostData("");
                }
            }
        });

        fetchPostData("");
        return view;
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
            postList.clear();
            postAdaptor.notifyDataSetChanged();
            fetchPostData("");
        }

        private void fetchPostData(String query) {
            isLoading = true;

            new Thread(() -> {
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .cookieJar(new PersistentCookieJar(getContext()))
                        .build();

                SearchRequest searchRequest = new SearchRequest();
                searchRequest.setCurrent(currentPage);
                searchRequest.setPageSize(pageSize);
                searchRequest.setSearchText(query);
                searchRequest.setSortField("");
                searchRequest.setTagList(new ArrayList<>());

                Gson gson = new Gson();
                String json = gson.toJson(searchRequest);

                RequestBody requestBody = RequestBody.create(
                        json,
                        MediaType.get("application/json; charset=utf-8")
                );

                Request request = new Request.Builder()
                        .url(UrlConstant.baseUrl + "/api/post/listAllPostBySearch")
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

                List<Post> records = gson.fromJson(responseBody, PostResponse.class).getData().getRecords();

                new Handler(Looper.getMainLooper()).post(() -> {
                    postList.addAll(records);
                    postAdaptor.notifyDataSetChanged();
                    isLoading = false;
                    if (records.size() < pageSize) {
                        isLastPage = true;
                    } else {
                        isLoading = false;
                    }
                });
            }).start();
        }

        public void performSearch(String query) {
            currentPage = 1;
            isLastPage = false;
            postList.clear();
            postAdaptor.notifyDataSetChanged();
            fetchPostData(query);
        }
}


