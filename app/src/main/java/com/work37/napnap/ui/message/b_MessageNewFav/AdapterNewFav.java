package com.work37.napnap.ui.message.b_MessageNewFav;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.work37.napnap.R;
import com.work37.napnap.detail.PostDetailActivity;
import com.work37.napnap.entity.Post;
import com.work37.napnap.global.PersistentCookieJar;
import com.work37.napnap.global.UrlConstant;
import com.work37.napnap.ui.message.b_MessageNewFans.AdapterNewFans;
import com.work37.napnap.ui.userlogin_register.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class AdapterNewFav extends RecyclerView.Adapter<AdapterNewFav.ViewHolder> {

    private Context context;
    private List<NewFav> newFavList;
    private ProgressBar progressBar;
    private String messageClass;

    Map<Long, Post> postCache = new HashMap<>();

    public AdapterNewFav(Context context, List<NewFav> newFavList, ProgressBar progressBar){
        this.context = context;
        this.newFavList = newFavList;
        this.progressBar = progressBar;
    }
    public AdapterNewFav(Context context, List<NewFav> newFavList, ProgressBar progressBar, String messageClass){
        this.context = context;
        this.newFavList = newFavList;
        this.progressBar = progressBar;
        this.messageClass = messageClass;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_newfav, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        NewFav newFav = newFavList.get(position);

        if(!newFav.getUserAvatar().isEmpty()){
            Glide.with(context)
                    .load(newFav.getUserAvatar())
                    .into(holder.userAvatar);
        }
        holder.userName.setText(newFav.getUserName());
        holder.userAccount.setText(newFav.getUserAccount());

        if (messageClass.equals("NewLike")){
        holder.message_class.setText("点赞了：");}

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String formattedCreateTime = sdf.format(newFav.getCreateTime());
        holder.createTime.setText(formattedCreateTime);

        holder.viewButton.setOnClickListener(v->{

            progressBar.setVisibility(View.VISIBLE);

            CompletableFuture<Void> voidCompletableFuture1 = CompletableFuture.runAsync(()->loadPostAsync(newFav.getPostId()));
            try {
                voidCompletableFuture1.get();
                Post post = postCache.get(newFav.getPostId());
                if(post!=null){
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("Post", post);
                    if (!(context instanceof Activity)) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    context.startActivity(intent);

                }


            } catch (Exception e){
                throw new RuntimeException(e);
            }
            progressBar.setVisibility(View.GONE);


        });



        holder.postTitle.setText(newFav.getPostTitle());
    }



    @Override
    public int getItemCount(){
        return newFavList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView userAvatar;
        TextView userName;
        TextView userAccount;
        TextView createTime;
        Button viewButton;

        TextView postTitle;
        TextView message_class;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            userName = itemView.findViewById(R.id.userName);
            userAccount = itemView.findViewById(R.id.userAccount);
            createTime = itemView.findViewById(R.id.createTime);
            viewButton = itemView.findViewById(R.id.viewButton);

            postTitle = itemView.findViewById(R.id.postTitle);
            message_class = itemView.findViewById(R.id.message_class);
        }
    }

    private void loadPostAsync(Long id){
        try {
            // Create JSON object
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("postId", id);

            RequestBody requestBody = RequestBody.create(
                    jsonObject1.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cookieJar(new PersistentCookieJar(context))
                    .build();
            Request request = new Request.Builder()
                    .url(UrlConstant.baseUrl + "api/post/getPostById")
                    .post(requestBody)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            String responseBody = response.body().string();
            JSONObject jsonObject = new JSONObject(responseBody);
            int code = jsonObject.getInt("code");
            if (code == 0) {
                JSONObject postData = jsonObject.getJSONObject("data");
                Long userId = postData.getLong("userId"); // 发帖用户id
                String title = postData.getString("title"); // 标题
                String content = postData.getString("content"); // 内容
                int likes = postData.getInt("likes"); // 点赞数
                int collectNum = postData.getInt("collectNum"); // 收藏数

                JSONArray picturesSrc = postData.getJSONArray("pictures");
                List<String> pictures = jsonArrayToList(picturesSrc);//图片地址

                JSONArray tagSrc = postData.getJSONArray("tag");
                List<String> tag = jsonArrayToList(tagSrc);

                String createTimeSrc = postData.getString("createTime");
                Date createTime = parseIso8601Date(createTimeSrc);

                Post post = new Post(id, userId, title, content, pictures, tag, likes, collectNum, createTime);

                postCache.put(id, post);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> jsonArrayToList(JSONArray jsonArray) throws JSONException {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getString(i));
        }
        return list;
    }

    public static Date parseIso8601Date(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.parse(dateString);
    }
}
