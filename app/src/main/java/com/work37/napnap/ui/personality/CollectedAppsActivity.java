package com.work37.napnap.ui.personality;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.work37.napnap.Adaptor.GameAdaptor;
import com.work37.napnap.Game.GameRequest;
import com.work37.napnap.ui.search.GameResponse;
import com.work37.napnap.R;
import com.work37.napnap.entity.Game;
import com.work37.napnap.global.PersistentCookieJar;
import com.work37.napnap.global.PublicActivity;
import com.work37.napnap.global.UrlConstant;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CollectedAppsActivity extends PublicActivity {
    private RecyclerView recyclerView;
    private GameAdaptor gameAdaptor;
    private List<Game> collectedGameList;
    private boolean isLoading = false;
    private int currentPage = 1;
    private int pageSize = 10;
    private boolean isLastPage = false;

    private boolean isFirstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collected_apps);

        recyclerView = findViewById(R.id.recycler_view);
        ImageButton backButton = findViewById(R.id.backButton);

        // Set up back button
        backButton.setOnClickListener(v -> finish());

        // Initialize game list and adapter
        collectedGameList = new ArrayList<>();
        gameAdaptor = new GameAdaptor(this, collectedGameList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(gameAdaptor);

        // Add scroll listener to RecyclerView for pagination
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == collectedGameList.size() - 1 && !isLoading && !isLastPage) {
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
            refreshGameList();
        } else {
            isFirstLoad = false;
        }
    }

    private void refreshGameList() {
        currentPage = 1;
        isLastPage = false;
        collectedGameList.clear();
        gameAdaptor.notifyDataSetChanged();
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

            GameRequest gameRequest = new GameRequest();
            gameRequest.setCurrent(currentPage);
            gameRequest.setPageSize(pageSize);
            gameRequest.setSortField("");
            Gson gson = new Gson();
            String json = gson.toJson(gameRequest);


            RequestBody requestBody = RequestBody.create(
                    json,
                    MediaType.get("application/json; charset=utf-8")
            );
            Request request = new Request.Builder()
                    .url(UrlConstant.baseUrl + "api/game/listAllGameByUserCollect")
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

            GameResponse gameResponse = gson.fromJson(responseBody, GameResponse.class);

            if (gameResponse.getCode() == 0) {
                List<Game> records = gameResponse.getData().getRecords();

                new Handler(Looper.getMainLooper()).post(() -> {
                    collectedGameList.addAll(records);
                    gameAdaptor.notifyDataSetChanged();
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
