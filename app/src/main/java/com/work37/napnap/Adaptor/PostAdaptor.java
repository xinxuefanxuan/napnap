package com.work37.napnap.Adaptor;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.work37.napnap.R;
import com.work37.napnap.detail.PostDetailActivity;
import com.work37.napnap.entity.Post;
import com.work37.napnap.global.PersistentCookieJar;
import com.work37.napnap.global.UrlConstant;
import com.work37.napnap.ui.userlogin_register.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostAdaptor extends RecyclerView.Adapter<PostAdaptor.ViewHolder> {
    private Context context;
    private List<Post> postList;
    private Map<Long, User> userCache = new HashMap<>();

    public PostAdaptor(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
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
        User cachedUser = userCache.get(userId);
        if (cachedUser != null) {
            bindUserToView(holder, cachedUser);
        } else {
            holder.postUsername.setText("Loading...");
            holder.userAvatar.setImageResource(R.drawable.baseline_person_24);
            loadUser(userId, holder);
        }

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

    private void loadUser(long userId, ViewHolder holder) {
        new Thread(() -> {
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
                    User user = new User(userId, username, userAvatar);
                    userCache.put(userId, user);

                    new Handler(Looper.getMainLooper()).post(() -> {
                        bindUserToView(holder, user);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            postUsername = itemView.findViewById(R.id.postUsername);
            postTimestamp = itemView.findViewById(R.id.postTimestamp);
            postTags = itemView.findViewById(R.id.postTags);
            postTitle = itemView.findViewById(R.id.postTitle);
            postContent = itemView.findViewById(R.id.postContent);
            postImage = itemView.findViewById(R.id.postImage);
        }
    }
}


