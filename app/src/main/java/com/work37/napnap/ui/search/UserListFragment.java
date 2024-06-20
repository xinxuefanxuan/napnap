package com.work37.napnap.ui.search;

import static com.work37.napnap.global.PublicApplication.getContext;

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
import com.work37.napnap.Adaptor.UserAdaptor;
import com.work37.napnap.R;
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

public class UserListFragment extends Fragment {
    private RecyclerView recyclerView;
    private UserAdaptor userAdaptor;
    private List<User> userList;
    private boolean isLoading = false;
    private int currentPage = 1;
    private int pageSize = 10;
    private boolean isLastPage = false;
    private boolean isFirstLoad = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        userList = new ArrayList<>();
        userAdaptor = new UserAdaptor(getContext(), userList);
        recyclerView.setAdapter(userAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == userList.size() - 1 && !isLoading && !isLastPage) {
                    currentPage++;
                    fetchUserData("");
                }
            }
        });

        fetchUserData("");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFirstLoad) {
            refreshUserList();
        } else {
            isFirstLoad = false;
        }
    }

    private void refreshUserList() {
        currentPage = 1;
        isLastPage = false;
        userList.clear();
        userAdaptor.notifyDataSetChanged();
        fetchUserData("");
    }

    private void fetchUserData(String query) {
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
                    .url(UrlConstant.baseUrl + "/api/user/listAllUserBySearch")
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

            List<User> records = gson.fromJson(responseBody,UserResponse.class).getData().getRecords();

            new Handler(Looper.getMainLooper()).post(() -> {
                userList.addAll(records);
                userAdaptor.notifyDataSetChanged();
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
        userList.clear();
        userAdaptor.notifyDataSetChanged();
        fetchUserData(query);
    }
}

