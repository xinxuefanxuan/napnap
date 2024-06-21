package com.work37.napnap.Adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.work37.napnap.R;
import com.work37.napnap.entity.CommentAndUser;
import com.work37.napnap.entity.CommentUnderPostVO;

import org.w3c.dom.Comment;

import java.util.List;

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
        }
        holder.username.setText(commentAndUser.getUsername());
        holder.commentContent.setText(commentAndUser.getContent());

        List<CommentAndUser> commentAndUserList = commentAndUser.getCommentAndUserList();
        if (commentAndUserList != null && !commentAndUserList.isEmpty()) {
            holder.repliesRecyclerView.setVisibility(View.VISIBLE);
            RepliesAdapter repliesAdapter = new RepliesAdapter(commentAndUserList, context);
            holder.repliesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            holder.repliesRecyclerView.setAdapter(repliesAdapter);
        } else {
            holder.repliesRecyclerView.setVisibility(View.GONE);
        }

        // Bind comment data to views
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
}
