package com.work37.napnap;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.work37.napnap.Adaptor.GameAdaptor;
import com.work37.napnap.Game.GameRequest;
import com.work37.napnap.Game.GameResponse;
import com.work37.napnap.entity.Game;
import com.work37.napnap.global.PersistentCookieJar;
import com.work37.napnap.global.UrlConstant;
import com.work37.napnap.service.ApiServices;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class GameListFragment extends Fragment {

    private static final String ARG_TYPE = "type";
    private String type;

    private RecyclerView recyclerView;
    private GameAdaptor gameAdaptor;
    private List<Game> gameList;
    private boolean isLoading = false;
    private int currentPage = 1;
    private int pageSize = 10;
    private boolean isLastPage = false;

    public static GameListFragment newInstance(String type) {
        GameListFragment fragment = new GameListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        // Initialize game list and adapter
        gameList = new ArrayList<>();
        gameAdaptor = new GameAdaptor(getContext(), gameList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(gameAdaptor);

        // Add scroll listener to RecyclerView for pagination
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == gameList.size() - 1 && !isLoading && !isLastPage) {
                    currentPage++;
                    try {
                        fetchGameData(type);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        // Fetch initial data
        try {
            fetchGameData(type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return view;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void fetchGameData(String type) throws IOException, JSONException {
        isLoading = true;

        // Create Retrofit instance
        new Thread(()->{
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cookieJar(new PersistentCookieJar(getContext()))
                    .build();
            // Create request
            GameRequest gameRequest = new GameRequest();
            gameRequest.setCurrent(currentPage);
            gameRequest.setPageSize(pageSize);
            gameRequest.setSearchText("");
            Log.d("zzzaaa",getSortFieldForType(type));
            String sortField = type.equals("热门榜")?"download":"score";
            gameRequest.setSortField(sortField);
            gameRequest.setTagList(new ArrayList<>());

            Gson gson = new Gson();
            String json = gson.toJson(gameRequest);

            RequestBody requestBody = RequestBody.create(
                    json,
                    MediaType.get("application/json; charset=utf-8")
            );
            Request request = new Request.Builder()
                    .url(UrlConstant.baseUrl+"api/game/listAllGameBySearch")
                    .post(requestBody)
                    .build();
            Response response = null;
            String responseBody = null;
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
                    gameList.addAll(records);
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

//        Retrofit retrofit = new Retrofit.Builder().baseUrl(UrlConstant.baseUrl+"api/game/listAllGameBySearch/").addConverterFactory(GsonConverterFactory.create()).build();
//        // 2. 解析http请求返回的json
//        ApiServices apiService = retrofit.create(ApiServices.class);
//
//        Call<GameResponse> gameResponseCall = apiService.listAllGames(gameRequest);
//        gameResponseCall.enqueue(new Callback<GameResponse>() {
//            @Override
//            public void onResponse(Call<GameResponse> call, retrofit2.Response<GameResponse> response) {
//                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 0) {
//                    List<Game> newGames = response.body().getData().getRecords();
//                    gameList.addAll(newGames);
//                    gameAdaptor.notifyDataSetChanged();
//                    isLoading = false;
//                    if (newGames.size() < pageSize) {
//                        isLastPage = true;
//                    }
//                } else {
//                    isLoading = false;
//                    // Handle error
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GameResponse> call, Throwable t) {
//                isLoading = false;
//                // Handle failure
//            }
//        });
    }

    private String getSortFieldForType(String type) {
        if (type.equals(getString(R.string.high_recommend))) {
            return "collectNum";
        } else if (type.equals(getString(R.string.popular_list))) {
            return "downloadNum";
        } else if (type.equals(getString(R.string.highScore_list))) {
            return "gameScore";
        } else {
            return "";
        }
    }
}

