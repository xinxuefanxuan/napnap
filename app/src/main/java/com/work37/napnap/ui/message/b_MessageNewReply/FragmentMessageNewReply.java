package com.work37.napnap.ui.message.b_MessageNewReply;

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
import com.work37.napnap.ui.message.b_MessageNewFav.AdapterNewFav;
import com.work37.napnap.ui.message.b_MessageNewFav.NewFav;
import com.work37.napnap.ui.message.b_MessageNewFav.NewFavResponse;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FragmentMessageNewReply extends Fragment {

    private static final String ARG_TYPE = "type";
    private String type;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView noMessage;

    private AdapterNewReply adapterNewReply;

    private List<NewReply> newReplyList;

    private boolean isLoading = false;
    private int currentPage = 1;
    private int pageSize = 10;
    private boolean isLastPage = false;
    private boolean isFirstLoad = true; // 标志位，标记是否是首次加载

    public static FragmentMessageNewReply newInstance(String type){
        FragmentMessageNewReply fragment = new FragmentMessageNewReply();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            type = getArguments().getString(ARG_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_list_general, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        noMessage = view.findViewById(R.id.no_message);

        newReplyList = new ArrayList<>();
        adapterNewReply = new AdapterNewReply(getContext(), newReplyList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapterNewReply);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == newReplyList.size() - 1 && !isLoading && !isLastPage) {
                    currentPage++;
                    try {
                        fetchNewReplyData(type);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        try {
            if(isFirstLoad){
                fetchNewReplyData(type);
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
            refreshNewReplyList();
        } else {
            isFirstLoad = false;
        }
    }

    private void refreshNewReplyList() {
        currentPage = 1;
        isLastPage = false;
        newReplyList.clear();
        adapterNewReply.notifyDataSetChanged();
        try {
            fetchNewReplyData(type);
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void fetchNewReplyData(String type) throws IOException, JSONException {
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


            Request request = new Request.Builder()
                    .url(UrlConstant.baseUrl+"api/message/listMessageByComment")
                    .post(requestBody)
                    .build();
            try {
                response = okHttpClient.newCall(request).execute();
                responseBody = response.body().string();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            //执行响应
            NewReplyResponse newReplyResponse = gson.fromJson(responseBody, NewReplyResponse.class);

            //处理响应数据
            if (newReplyResponse.getCode() == 0) {
                List<NewReply> records = newReplyResponse.getData().getRecords();
                new Handler(Looper.getMainLooper()).post(() -> {
                    newReplyList.addAll(records);
                    adapterNewReply.notifyDataSetChanged();

                    isLoading = false;
                    progressBar.setVisibility(View.GONE);
                    if(newReplyList.isEmpty()){
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