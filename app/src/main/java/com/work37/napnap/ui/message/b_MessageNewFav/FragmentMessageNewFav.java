package com.work37.napnap.ui.message.b_MessageNewFav;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.work37.napnap.R;
import com.work37.napnap.global.PersistentCookieJar;
import com.work37.napnap.global.UrlConstant;
import com.work37.napnap.ui.message.MessageRequest;
import com.work37.napnap.ui.message.b_MessageNewFans.AdapterNewFans;
import com.work37.napnap.ui.message.b_MessageNewFans.FragmentMessageNewFans;
import com.work37.napnap.ui.message.b_MessageNewFans.NewFans;
import com.work37.napnap.ui.message.b_MessageNewFans.NewFansResponse;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FragmentMessageNewFav extends Fragment {
    private static final String ARG_TYPE = "type";
    private String type;
    private static final String Message_Class = "message_class";
    private String message_class;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView noMessage;

    private AdapterNewFav adapterNewFav;

    private List<NewFav> newFavList;

    private boolean isLoading = false;
    private int currentPage = 1;
    private int pageSize = 10;
    private boolean isLastPage = false;
    private boolean isFirstLoad = true; // 标志位，标记是否是首次加载

    public static FragmentMessageNewFav newInstance(String type) {
        FragmentMessageNewFav fragment = new FragmentMessageNewFav();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        args.putString(Message_Class, "newFav");
        fragment.setArguments(args);
        return fragment;
    }

    public static FragmentMessageNewFav newInstance(String type, String message_class) {
        FragmentMessageNewFav fragment = new FragmentMessageNewFav();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        args.putString(Message_Class, message_class);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE);
            message_class = getArguments().getString(Message_Class);
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_list_general, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        noMessage = view.findViewById(R.id.no_message);

        //初始化游戏列表和适配器
        newFavList = new ArrayList<>();
        adapterNewFav = new AdapterNewFav(getContext(), newFavList, progressBar, message_class);

        //初始化recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapterNewFav);

        // 添加分割线
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);


        //为 RecyclerView 添加滚动监听器以实现分页
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == newFavList.size() - 1 && !isLoading && !isLastPage) {
                    currentPage++;
                    try {
                        fetchNewFavData(type);
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
                fetchNewFavData(type);
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
            refreshNewFansList();
        } else {
            isFirstLoad = false;
        }
    }

    private void refreshNewFansList() {
        currentPage = 1;
        isLastPage = false;
        newFavList.clear();
        adapterNewFav.notifyDataSetChanged();
        try {
            fetchNewFavData(type);
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void fetchNewFavData(String type) throws IOException, JSONException {
        isLoading = true;
        getActivity().runOnUiThread(() -> {
            progressBar.setVisibility(View.VISIBLE);
            noMessage.setVisibility(View.GONE);
        });

        // Create Retrofit instance
        new Thread(()->{
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cookieJar(new PersistentCookieJar(getContext()))
                    .build();

            //创建请求
            int isVisiable = type.equals("未读")?0:1;
            MessageRequest messageRequest = new MessageRequest(isVisiable,currentPage,pageSize);

            //将请求对象转换为JSON字符串
            Gson gson = new Gson();
            String json = gson.toJson(messageRequest);

            //创建请求体
            RequestBody requestBody = RequestBody.create(
                    json,
                    MediaType.get("application/json; charset=utf-8")
            );

            //执行请求
            Response response = null;
            String responseBody = null;

            if (message_class.equals("NewLike")){
                Request request = new Request.Builder()
                        .url(UrlConstant.baseUrl+"api/message/listMessageByLike")
                        .post(requestBody)
                        .build();
                try {
                    response = okHttpClient.newCall(request).execute();
                    responseBody = response.body().string();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
            Request request = new Request.Builder()
                    .url(UrlConstant.baseUrl+"api/message/listMessageByCollect")
                    .post(requestBody)
                    .build();
                try {
                    response = okHttpClient.newCall(request).execute();
                    responseBody = response.body().string();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }



            //执行响应
            NewFavResponse newFavResponse = gson.fromJson(responseBody, NewFavResponse.class);

            //处理响应数据
            if (newFavResponse.getCode() == 0) {
                List<NewFav> records = newFavResponse.getData().getRecords();
                new Handler(Looper.getMainLooper()).post(() -> {
                    newFavList.addAll(records);
                    adapterNewFav.notifyDataSetChanged();

                    isLoading = false;
                    progressBar.setVisibility(View.GONE);
                    if(newFavList.isEmpty()){
                        noMessage.setVisibility(View.VISIBLE);
                    }

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


