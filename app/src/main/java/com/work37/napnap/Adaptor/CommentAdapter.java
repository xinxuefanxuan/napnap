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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.work37.napnap.Game.CommentRequest;
import com.work37.napnap.R;
import com.work37.napnap.entity.CommentAndUser;
import com.work37.napnap.entity.CommentUnderPostVO;
import com.work37.napnap.global.PersistentCookieJar;
import com.work37.napnap.global.PublicApplication;
import com.work37.napnap.global.UrlConstant;
import com.work37.napnap.ui.userlogin_register.User;

import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<CommentAndUser> commentList;

    private Context context;

    public CommentAdapter(List<CommentAndUser> commentList,Context context) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentAndUser commentAndUser = commentList.get(position);
        if(commentAndUser.getUserAvatar()!=null){
            Glide.with(context).load(commentAndUser.getUserAvatar())
                    .into(holder.userAvatar);
        }else{
            holder.userAvatar.setVisibility(View.GONE);
        }
        holder.username.setText(commentAndUser.getUsername());
        holder.commentContent.setText(commentAndUser.getContent());

        holder.commentContent.setOnClickListener(v -> {
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
                                postReply(commentAndUser, replyContent, holder.repliesRecyclerView);
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


        List<CommentAndUser> commentAndUserList = commentAndUser.getCommentAndUserList();
        if (commentAndUserList != null && !commentAndUserList.isEmpty()) {
            holder.repliesRecyclerView.setVisibility(View.VISIBLE);
            RepliesAdapter repliesAdapter = new RepliesAdapter(commentAndUserList, context);
            holder.repliesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            holder.repliesRecyclerView.setAdapter(repliesAdapter);
        } else {
            holder.repliesRecyclerView.setVisibility(View.GONE);
        }
    }

    private void postReply(CommentAndUser commentAndUser, String replyContent, RecyclerView repliesRecyclerView) {
        new Thread(() -> {
            try {
                ArrayList<String> list = new ArrayList<>();
                CommentRequest commentRequest = new CommentRequest();
                commentRequest.setComment(replyContent);
                commentRequest.setPicture(list);
                commentRequest.setType(1);
                commentRequest.setParentId(commentAndUser.getPostId());

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
                        CommentAndUser newComment = new CommentAndUser();
                        newComment.setUserAvatar(PublicApplication.getCurrentUser().getUserAvatar());
                        newComment.setUsername(PublicApplication.getCurrentUser().getUserName());
                        newComment.setContent(replyContent);
                        User parent = new User();
                        parent.setUserName(commentAndUser.getUsername());
                        newComment.setParentUser(parent);
                        newComment.setCommentAndUserList(new ArrayList<>());

                        // 添加到评论列表中
                        List<CommentAndUser> commentAndUserList = commentAndUser.getCommentAndUserList();
//                        commentAndUserList.add(newComment);

                        // 获取适配器实例并调用 addReply 方法
                        RepliesAdapter repliesAdapter = (RepliesAdapter) repliesRecyclerView.getAdapter();
                        if (repliesAdapter != null) {
                            repliesAdapter.addReply(newComment);
                        }else{
                            List<CommentAndUser> newlist = new ArrayList<>();
                            newlist.add(newComment); // 添加新评论到列表中
                            RepliesAdapter newRepliesAdapter = new RepliesAdapter(newlist, context);
                            repliesRecyclerView.setAdapter(newRepliesAdapter); // 设置新的适配器到 RecyclerView
                        }

                        // 隐藏键盘
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(repliesRecyclerView.getWindowToken(), 0);
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
        return commentList.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatar;
        TextView username;
        TextView commentContent;
        RecyclerView repliesRecyclerView;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            username = itemView.findViewById(R.id.username);
            commentContent = itemView.findViewById(R.id.commentContent);
            repliesRecyclerView = itemView.findViewById(R.id.repliesRecyclerView);
        }
    }

    public void addComment(CommentAndUser commentAndUser) {
        commentList.add(0,commentAndUser);
        notifyItemInserted(0);
    }
}
