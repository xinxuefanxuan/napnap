package com.work37.napnap.ui.frontPage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.work37.napnap.RequestAndResponse.GameRequest;
import com.work37.napnap.RequestAndResponse.GameResponse;
import com.work37.napnap.entity.Game;
import com.work37.napnap.global.PersistentCookieJar;
import com.work37.napnap.global.UrlConstant;

import org.json.JSONException;

import java.io.IOException;
import java.net.SocketException;
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
    private ProgressBar progressBar;
    private boolean isLoading = false;
    private int currentPage = 1;
    private int pageSize = 10;
    private boolean isLastPage = false;
    private boolean isFirstLoad = true; // 标志位，标记是否是首次加载

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
        progressBar = view.findViewById(R.id.progressBar);

        //初始化游戏列表和适配器
        gameList = new ArrayList<>();
        gameAdaptor = new GameAdaptor(getContext(), gameList);

        //初始化recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(gameAdaptor);

        // 添加分割线
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);


        //为 RecyclerView 添加滚动监听器以实现分页
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

        //获取初始数据
        try {
            if(isFirstLoad){
                fetchGameData(type);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

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
        gameAdaptor.notifyDataSetChanged();
        try {
            fetchGameData(type);
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void fetchGameData(String type) throws IOException, JSONException {
        isLoading = true;
        getActivity().runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));

        // Create Retrofit instance
        new Thread(()->{
            try {
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .cookieJar(new PersistentCookieJar(getContext()))
                        .build();

                //创建请求
                GameRequest gameRequest = new GameRequest();
                gameRequest.setCurrent(currentPage);
                gameRequest.setPageSize(pageSize);
                gameRequest.setSearchText("");
                String sortField = type.equals("热门榜")?"download":"score";
                gameRequest.setSortField(sortField);
                gameRequest.setTagList(new ArrayList<>());

                //将请求对象转换为JSON字符串
                Gson gson = new Gson();
                String json = gson.toJson(gameRequest);

                //创建请求体
                RequestBody requestBody = RequestBody.create(
                        json,
                        MediaType.get("application/json; charset=utf-8")
                );
                Request request = new Request.Builder()
                        .url(UrlConstant.baseUrl+"api/game/listAllGameBySearch")
                        .post(requestBody)
                        .build();

                //执行请求
                Response response = null;
                String responseBody = null;
                try {
                    response = okHttpClient.newCall(request).execute();
                    responseBody = response.body().string();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                //执行响应
                GameResponse gameResponse = gson.fromJson(responseBody, GameResponse.class);

                //处理响应数据
                if (gameResponse.getCode() == 0) {
                    List<Game> records = gameResponse.getData().getRecords();
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
                }
            }catch (Exception e) {
                Log.e("TAG", "Exception: " + e.getMessage());
            }

        }).start();
    }
//    private void retryFetchGameData() {
//        new Handler(Looper.getMainLooper()).postDelayed(this::fetchGameData, 3000);
//    }
}

