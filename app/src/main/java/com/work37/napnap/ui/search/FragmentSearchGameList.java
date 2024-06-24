package com.work37.napnap.ui.search;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.work37.napnap.Adaptor.GameAdaptor;
import com.work37.napnap.R;
import com.work37.napnap.RequestAndResponse.GameResponse;
import com.work37.napnap.RequestAndResponse.SearchRequest;
import com.work37.napnap.entity.Game;
import com.work37.napnap.global.PersistentCookieJar;
import com.work37.napnap.global.UrlConstant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FragmentSearchGameList extends Fragment {
    private RecyclerView recyclerView;
    private GameAdaptor gameAdaptor;
    private List<Game> gameList;
    private ProgressBar progressBar;
    private boolean isLoading = false;
    private int currentPage = 1;
    private int pageSize = 10;
    private boolean isLastPage = false;
    private boolean isFirstLoad = true;
    private boolean hasMoreData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_general_search_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);

        gameList = new ArrayList<>();
        gameAdaptor = new GameAdaptor(getContext(), gameList);

        //初始化recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(gameAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        hasMoreData = true;

        // 添加分割线
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == gameList.size() - 1 && !isLoading && !isLastPage&&hasMoreData) {
                    currentPage++;
                    fetchGameData("");
                }
            }
        });

        fetchGameData("");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFirstLoad) {
            refreshGameList();
        } else {
            isFirstLoad = false;
        }
    }

    private void refreshGameList() {
        currentPage = 1;
        isLastPage = false;
        gameList.clear();
        hasMoreData = true;
        gameAdaptor.notifyDataSetChanged();
        fetchGameData("");
    }

    private void fetchGameData(String query) {
        isLoading = true;
        getActivity().runOnUiThread(()->progressBar.setVisibility(View.VISIBLE));

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
                    .url(UrlConstant.baseUrl + "/api/game/listAllGameBySearch")
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

            List<Game> records = gson.fromJson(responseBody, GameResponse.class).getData().getRecords();
            if(records.size()<10){
                hasMoreData = false;
            }
            new Handler(Looper.getMainLooper()).post(() -> {
                gameList.addAll(records);
                gameAdaptor.notifyDataSetChanged();
                isLoading = false;
                progressBar.setVisibility(View.GONE);
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
        gameList.clear();
        gameAdaptor.notifyDataSetChanged();
        fetchGameData(query);
    }
}