package com.work37.napnap.Adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.work37.napnap.R;
import com.work37.napnap.entity.CommentAndUser;

import java.util.List;

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

        if (reply.getUserAvatar() != null) {
            Glide.with(context).load(reply.getUserAvatar())
                    .into(holder.userAvatar);
        }
        holder.username.setText(reply.getUsername());
        holder.replyContent.setText(reply.getContent());
    }

    @Override
    public int getItemCount() {
        return repliesList.size();
    }

    static class ReplyViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatar;
        TextView username;
        TextView replyContent;

        public ReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            username = itemView.findViewById(R.id.username);
            replyContent = itemView.findViewById(R.id.replyContent);
        }
    }
}
