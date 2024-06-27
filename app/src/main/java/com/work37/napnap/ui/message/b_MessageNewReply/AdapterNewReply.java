package com.work37.napnap.ui.message.b_MessageNewReply;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.work37.napnap.R;
import com.work37.napnap.entity.Post;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterNewReply extends RecyclerView.Adapter<AdapterNewReply.ViewHolder>{
    private Context context;
    private List<NewReply> newReplyList;
    private ProgressBar progressBar;

    Map<Long, Post> postCache = new HashMap<>();

    public AdapterNewReply(Context context, List<NewReply> newReplyList){
        this.context = context;
        this.newReplyList = newReplyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_newreply, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        NewReply newReply = newReplyList.get(position);

        if(!newReply.getUserAvatar().isEmpty()){
            Glide.with(context)
                    .load(newReply.getUserAvatar())
                    .into(holder.userAvatar);
        }
        holder.userName.setText(newReply.getUserName());
        holder.userAccount.setText(newReply.getUserAccount());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String formattedCreateTime = sdf.format(newReply.getCreateTime());
        holder.createTime.setText(formattedCreateTime);

        if(newReply.getCommentParentContent().isEmpty()){
            holder.postTitle.setText(newReply.getPostTitle());
        } else {
            String parentContent = newReply.getCommentParentContent() + " in " + newReply.getPostTitle();
            holder.postTitle.setText(parentContent);
        }

        holder.replyContent.setText(newReply.getCommentContent());


    }

    @Override
    public int getItemCount(){
        return newReplyList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView userAvatar;
        TextView userName;
        TextView userAccount;
        TextView createTime;
        Button viewButton;

        TextView postTitle;
        TextView replyContent;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            userName = itemView.findViewById(R.id.userName);
            userAccount = itemView.findViewById(R.id.userAccount);
            createTime = itemView.findViewById(R.id.createTime);
            viewButton = itemView.findViewById(R.id.viewButton);

            postTitle = itemView.findViewById(R.id.postTitle);
            replyContent = itemView.findViewById(R.id.replyContent);
        }
    }
}
