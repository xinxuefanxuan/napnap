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
import com.work37.napnap.entity.Post;

import java.util.List;

public class PostAdaptor extends RecyclerView.Adapter<PostAdaptor.ViewHolder> {
    private Context context;
    private List<Post> postList;

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
        // Load other post details
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView postTitle;
        TextView postContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postTitle = itemView.findViewById(R.id.postTitle);
            postContent = itemView.findViewById(R.id.postContent);
        }
    }
}


