package com.work37.napnap.ui.findgame;

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
import com.work37.napnap.Game.GameRequest;
import com.work37.napnap.databinding.FragmentTagfilteredGamelistBinding;
import com.work37.napnap.entity.Game;
import com.work37.napnap.global.PersistentCookieJar;
import com.work37.napnap.global.UrlConstant;
import com.work37.napnap.ui.search.GameResponse;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FragmentFindGameList extends Fragment {
    private FragmentTagfilteredGamelistBinding binding;
    private static final String ARG_TAGS = "tags";
    private List<String> tags;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private GameAdaptor gameAdaptor;
    private List<Game> gameListA;
    private boolean isLoading = false;
    private int currentPage = 1;
    private int pageSize = 10;
    private boolean isLastPage = false;

    public static FragmentFindGameList newInstance(List<String> tags) {
        FragmentFindGameList fragment = new FragmentFindGameList();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_TAGS, new ArrayList<>(tags));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tags = getArguments().getStringArrayList(ARG_TAGS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentTagfilteredGamelistBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerView =binding.recyclerView;
        progressBar = binding.progressBar;

        gameListA = new ArrayList<>();
        gameAdaptor = new GameAdaptor(getContext(), gameListA);

        //初始化recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(gameAdaptor);

        // 添加分割线
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        // Add scroll listener to RecyclerView for pagination
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == gameListA.size() - 1 && !isLoading && !isLastPage) {
                    currentPage++;
                    fetchGameData();
                }
            }
        });
        // Fetch initial data
        fetchGameData();

        return root;
    }

    public void updateTags(List<String> tags) {
        this.tags = tags;
        refreshGameList();
    }

    private void refreshGameList() {
        currentPage = 1;
        isLastPage = false;
        gameListA.clear();
        fetchGameData();
//        gameAdaptor.notifyDataSetChanged();
    }

    private void fetchGameData() {
        isLoading = true;
        getActivity().runOnUiThread(()->progressBar.setVisibility(View.VISIBLE));

        // Create Retrofit instance
        new Thread(() -> {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cookieJar(new PersistentCookieJar(getContext()))
                    .build();
            // Create request
            GameRequest gameRequest = new GameRequest();
            gameRequest.setCurrent(currentPage);
            gameRequest.setPageSize(pageSize);
            gameRequest.setSearchText("");
            gameRequest.setSortField(""); // Replace with actual sort field if needed
            gameRequest.setTagList(tags);

            Gson gson = new Gson();
            String json = gson.toJson(gameRequest);

            RequestBody requestBody = RequestBody.create(
                    json,
                    MediaType.get("application/json; charset=utf-8")
            );
            Request request = new Request.Builder()
                    .url(UrlConstant.baseUrl + "api/game/listAllGameBySearch")
                    .post(requestBody)
                    .build();
            Response response = null;
            String responseBody = null;
            try {
                response = okHttpClient.newCall(request).execute();
                responseBody = response.body().string();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            GameResponse gameResponse = gson.fromJson(responseBody, GameResponse.class);
            if (gameResponse.getCode() == 0) {
                List<Game> records = gameResponse.getData().getRecords();
                new Handler(Looper.getMainLooper()).post(() -> {
                    gameListA.addAll(records);
                    gameAdaptor.notifyDataSetChanged(); // 更新Adapter数据
                    isLoading = false;
                    progressBar.setVisibility(View.GONE);
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
