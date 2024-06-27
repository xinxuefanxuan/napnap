package com.work37.napnap.ui.message.b_MessageNewFans;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.work37.napnap.Adaptor.GameAdaptor;
import com.work37.napnap.R;
import com.work37.napnap.detail.UserDetailActivity;
import com.work37.napnap.entity.Game;
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

public class AdapterNewFans extends RecyclerView.Adapter<AdapterNewFans.ViewHolder> {

    private Context context;
    private List<NewFans> newFansList;

    Map<Long,User> userCache = new HashMap<>();

    public AdapterNewFans(Context context, List<NewFans> newFansList) {
        this.context = context;
        this.newFansList = newFansList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_newfans, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        NewFans newFans = newFansList.get(position);

        if(!newFans.getUserAvatar().isEmpty()){
            Glide.with(context)
                    .load(newFans.getUserAvatar())
                    .into(holder.userAvatar);
        }
        holder.userName.setText(newFans.getUserName());
        holder.userAccount.setText(newFans.getUserAccount());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String formattedCreateTime = sdf.format(newFans.getCreateTime());
        holder.createTime.setText(formattedCreateTime);

        holder.viewButton.setOnClickListener(v->{

            CompletableFuture<Void> voidCompletableFuture1 = CompletableFuture.runAsync(()->loadUserAsync(newFans.getId()));
            try {
                voidCompletableFuture1.get();
                User user = userCache.get(newFans.getId());
                if(user!=null){
                    Intent intent = new Intent(context, UserDetailActivity.class);
                    intent.putExtra("User", user);
                    if (!(context instanceof Activity)) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    context.startActivity(intent);
                }
            }catch (Exception e) {
                throw new RuntimeException(e);
            }


        });

    }

    private void loadUserAsync(Long id) {
        try {
            // Create JSON object
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("userId", id);

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
                String userAccount = userData.getString("userAccount");
                long fansNum = userData.getLong("fansNum");
                long focusNum = userData.getLong("focusNum");
                String userProfile = userData.getString("userProfile");
                User user = new User();
                user.setUserName(username);
                user.setUserAvatar(userAvatar);
                user.setUserAccount(userAccount);
                user.setFanNum(fansNum);
                user.setFollowNum(focusNum);
                user.setProfile(userProfile);
                userCache.put(id, user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount(){
        return newFansList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView userAvatar;
        TextView userName;
        TextView userAccount;
        TextView createTime;
        Button viewButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            userName = itemView.findViewById(R.id.userName);
            userAccount = itemView.findViewById(R.id.userAccount);
            createTime = itemView.findViewById(R.id.createTime);
            viewButton = itemView.findViewById(R.id.viewButton);
        }
    }

}
