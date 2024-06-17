package com.work37.napnap.ui.personality;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.work37.napnap.R;
import com.work37.napnap.entity.Post;

public class PostDetailActivity extends AppCompatActivity {

    private TextView postTitle, postContent, postLikes, postCollectNum;
    private ImageView postPicture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        postTitle = findViewById(R.id.post_detail_title);
        postContent = findViewById(R.id.post_detail_content);
        postLikes = findViewById(R.id.post_detail_likes);
        postCollectNum = findViewById(R.id.post_detail_collect_num);
        postPicture = findViewById(R.id.post_detail_picture);

        Post post = (Post) getIntent().getSerializableExtra("post");

        if (post != null) {
            postTitle.setText(post.getTitle());
            postContent.setText(post.getContent());
            postLikes.setText("点赞数：" + post.getLikes());
            postCollectNum.setText("收藏数：" + post.getCollectNum());
            if (!post.getPicture().isEmpty()) {
                Glide.with(this)
                        .load(post.getPicture().get(0))
                        .into(postPicture);
            }
        }
    }
}
