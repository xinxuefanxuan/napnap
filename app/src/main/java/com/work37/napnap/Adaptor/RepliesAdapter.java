package com.work37.napnap.Adaptor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.work37.napnap.RequestAndResponse.CommentRequest;
import com.work37.napnap.R;
import com.work37.napnap.entity.CommentAndUser;
import com.work37.napnap.global.PersistentCookieJar;
import com.work37.napnap.global.PublicApplication;
import com.work37.napnap.global.UrlConstant;
import com.work37.napnap.ui.userlogin_register.User;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RepliesAdapter extends RecyclerView.Adapter<RepliesAdapter.ReplyViewHolder> {
    private List<CommentAndUser> repliesList;
    private Context context;

    public RepliesAdapter(List<CommentAndUser> repliesList, Context context) {
        this.repliesList = repliesList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reply_item, parent, false);
        return new ReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        CommentAndUser reply = repliesList.get(position);

        Long parentId = reply.getParentId();


        if (reply.getUserAvatar() != null) {
            Glide.with(context).load(reply.getUserAvatar())
                    .into(holder.userAvatar);
        }
        holder.username.setText(reply.getUsername());
        holder.replyContent.setText(reply.getContent());
        holder.replyTo.setText("回复 "+reply.getParentUser().getUserName());

        holder.replyContent.setOnClickListener(v -> {
            // Check if context is an instance of Activity and the activity is not finishing
            if (context instanceof Activity && !((Activity) context).isFinishing()) {
                // Inflate the custom layout
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.alert_dialog_edit_text, null);
                final EditText input = dialogView.findViewById(R.id.editTextReply);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(dialogView)
                        .setTitle("Reply")
                        .setPositiveButton("Send", (dialog, which) -> {
                            String replyContent = input.getText().toString().trim();
                            if (!replyContent.isEmpty()) {
                                postReply(reply, replyContent, holder.getAdapterPosition());
                            }
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                AlertDialog dialog = builder.create();
                dialog.show();

                // Automatically show the keyboard
                input.requestFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
            }
        });
    }

    private void postReply(CommentAndUser originalComment, String replyContent, int position) {
        new Thread(() -> {
            try {
                ArrayList<String> list = new ArrayList<>();
                CommentRequest commentRequest = new CommentRequest();
                commentRequest.setComment(replyContent);
                commentRequest.setPicture(list);
                commentRequest.setType(1);
                commentRequest.setParentId(originalComment.getPostId());

                Gson gson = new Gson();
                String json = gson.toJson(commentRequest);
                RequestBody requestBody = RequestBody.create(
                        json,
                        MediaType.get("application/json; charset=utf-8")
                );

                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .cookieJar(new PersistentCookieJar(context.getApplicationContext()))
                        .build();
                Request request = new Request.Builder()
                        .url(UrlConstant.baseUrl + "api/comment/addComment")
                        .post(requestBody)
                        .build();
                Response response = okHttpClient.newCall(request).execute();
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                int code = jsonObject.getInt("code");
                if (code == 0) {
                    ((Activity) context).runOnUiThread(() -> {
                        // 创建新的评论对象
                        CommentAndUser newReply = new CommentAndUser();
                        newReply.setUserAvatar(PublicApplication.getCurrentUser().getUserAvatar());
                        newReply.setUsername(PublicApplication.getCurrentUser().getUserName());
                        newReply.setContent(replyContent);
                        User parent = new User();
                        parent.setUserName(originalComment.getUsername());
                        newReply.setParentUser(parent);
                        newReply.setCommentAndUserList(new ArrayList<>());

                        // 调用 addReply 方法添加新回复并更新 UI
                        addReply(newReply);

                        // 隐藏键盘
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(((Activity) context).getWindow().getDecorView().getWindowToken(), 0);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    @Override
    public int getItemCount() {
        return repliesList.size();
    }

    static class ReplyViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatar;
        TextView username;
        TextView replyContent;
        TextView replyTo;

        public ReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            username = itemView.findViewById(R.id.username);
            replyContent = itemView.findViewById(R.id.replyContent);
            replyTo = itemView.findViewById(R.id.reply);
        }
    }
    // 添加新的方法，用于更新数据
    public void addReply(CommentAndUser newReply) {
        repliesList.add(newReply);
        notifyItemInserted(repliesList.size() - 1);
    }
}
