package com.work37.napnap.Adaptor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.work37.napnap.R;
import com.work37.napnap.detail.PostDetailActivity;
import com.work37.napnap.entity.Post;
import com.work37.napnap.global.PersistentCookieJar;
import com.work37.napnap.global.UrlConstant;
import com.work37.napnap.ui.userlogin_register.User;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyPostAdaptor extends RecyclerView.Adapter<MyPostAdaptor.ViewHolder> {
    private Context context;
    private List<Post> postList;
    private Map<Long, User> userCache = new HashMap<>();

    public MyPostAdaptor(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.postTitle.setText(post.getTitle());
        holder.postContent.setText(post.getContent());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String formattedDate = sdf.format(post.getCreateTime());
        holder.postTimestamp.setText(formattedDate);
        holder.postTags.setText(formatTags(post.getTag()));
        Long userId = post.getUserId();
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(()->loadUserAsync(userId,holder));
        try {
            voidCompletableFuture.get();
            User cachedUser = userCache.get(userId);
            if (cachedUser != null) {
                bindUserToView(holder, cachedUser);
            }else {
                holder.postUsername.setText("Loading...");
                holder.userAvatar.setImageResource(R.drawable.baseline_person_24);
            }
        }catch (Exception e){
            throw  new RuntimeException(e);
        }
        holder.deleteButton.setOnClickListener(v -> {
            deletePost(post, position);
        });

        holder.cancelButton.setOnClickListener(v -> {
            holder.deleteButton.setVisibility(View.GONE);
            holder.cancelButton.setVisibility(View.GONE);
            holder.contentLayout.setVisibility(View.VISIBLE);
        });



        if (post.getPictures() != null && !post.getPictures().isEmpty()) {
            holder.postImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(post.getPictures().get(0))
                    .into(holder.postImage);
        } else {
            holder.postImage.setVisibility(View.GONE);
        }

        // Set click listener for the entire post item, excluding the user avatar
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("Post", post); // Pass post ID or any required data
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        });

    }


    private void bindUserToView(ViewHolder holder, User user) {
        holder.postUsername.setText(user.getUserName());
        if (user.getUserAvatar() != null) {
            holder.userAvatar.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(user.getUserAvatar())
                    .into(holder.userAvatar);
        } else {
            holder.userAvatar.setVisibility(View.GONE);
        }
    }

    private void loadUserAsync(long userId, ViewHolder holder) {
            try {
                // Create JSON object
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("userId", userId);

                RequestBody requestBody = RequestBody.create(
                        jsonObject1.toString(),
                        MediaType.get("application/json; charset=utf-8")
                );

                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .cookieJar(new PersistentCookieJar(context))
                        .build();
                Request request = new Request.Builder()
                        .url(UrlConstant.baseUrl + "api/user/getUserById")
                        .post(requestBody)
                        .build();
                Response response = okHttpClient.newCall(request).execute();
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                int code = jsonObject.getInt("code");
                if (code == 0) {
                    JSONObject userData = jsonObject.getJSONObject("data");
                    String userAvatar = userData.getString("userAvatar");
                    String username = userData.getString("userName");
                    User user = new User( username, userAvatar,userId);
                    userCache.put(userId, user);
                    ((Activity) context).runOnUiThread(() -> bindUserToView(holder, user));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    private String formatTags(List<String> tags) {
        StringBuilder sb = new StringBuilder();
        for (String tag : tags) {
            sb.append(tag).append("  ");
        }
        return sb.toString().trim();
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatar;
        TextView postUsername;
        TextView postTimestamp;
        TextView postTags;
        TextView postTitle;
        TextView postContent;
        ImageView postImage;

        public Button deleteButton;
        public Button cancelButton;
        public LinearLayout contentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            postUsername = itemView.findViewById(R.id.postUsername);
            postTimestamp = itemView.findViewById(R.id.postTimestamp);
            postTags = itemView.findViewById(R.id.postTags);
            postTitle = itemView.findViewById(R.id.postTitle);
            postContent = itemView.findViewById(R.id.postContent);
            postImage = itemView.findViewById(R.id.postImage);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            cancelButton = itemView.findViewById(R.id.cancelButton);
            contentLayout = itemView.findViewById(R.id.contentLayout);
        }
    }

    private void deletePost(Post post, int position) {
        new Thread(()->{
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("postId",post.getId());
                Gson gson = new Gson();
                String json = gson.toJson(jsonObject);
                RequestBody requestBody = RequestBody.create(
                        json,
                        MediaType.get("application/json; charset=utf-8")
                );
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .cookieJar(new PersistentCookieJar(context.getApplicationContext()))
                        .build();
                Request request = new Request.Builder()
                        .url(UrlConstant.baseUrl + "api/post/deletePostById")
                        .post(requestBody)
                        .build();
                Response response = okHttpClient.newCall(request).execute();
                String responseBody = response.body().string();
                JSONObject jsonObject1 = new JSONObject(responseBody);
                int code = jsonObject1.getInt("code");
                if(code==0){
                    postList.remove(position);
                    new Handler(Looper.getMainLooper()).post(() -> notifyItemRemoved(position));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();


    }
}



