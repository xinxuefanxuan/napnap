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

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> posts;
    private Context context;
    private OnPostClickListener onPostClickListener;

    public interface OnPostClickListener {
        void onPostClick(Post post);
    }

    public PostAdapter(List<Post> posts, OnPostClickListener onPostClickListener) {
        this.posts = posts;
        this.onPostClickListener = onPostClickListener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view, onPostClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.title.setText(post.getTitle());
        holder.username.setText(String.valueOf(post.getUserId()));
        // 使用 Glide 加载图片
        if (!post.getPicture().isEmpty()) {
            holder.picture.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(post.getPicture().get(0)) // 假设只显示第一张图片
                    .into(holder.picture);
        } else {
            holder.picture.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void addPosts(List<Post> newPosts) {
        int startPosition = posts.size();
        posts.addAll(newPosts);
        //通知适配器更新视图
        notifyItemRangeInserted(startPosition, newPosts.size());
    }

    public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        TextView username;
        ImageView picture;
        OnPostClickListener onPostClickListener;

        public PostViewHolder(@NonNull View itemView, OnPostClickListener onPostClickListener) {
            super(itemView);
            title = itemView.findViewById(R.id.post_title);
            username = itemView.findViewById(R.id.post_username);
            picture = itemView.findViewById(R.id.post_picture);
            this.onPostClickListener = onPostClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                onPostClickListener.onPostClick(posts.get(position));
            }
        }
    }
}


