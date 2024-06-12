package com.work37.napnap.Adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.work37.napnap.R;
import com.work37.napnap.entity.Game;

import java.util.List;

public class GameAdaptor extends RecyclerView.Adapter<GameAdaptor.ViewHolder> {
    private Context context;
    private List<Game> gameList;

    public GameAdaptor(Context context, List<Game> gameList) {
        this.context = context;
        this.gameList = gameList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_app, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Game game = gameList.get(position);

        holder.appId.setText(String.valueOf(game.getRank()));
        holder.appIcon.setImageDrawable(game.getPicture());
        holder.appName.setText(game.getName());

        holder.itemView.setOnClickListener(v->{
            Toast.makeText(context,"Clicked"+game.getName(),Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;

        Button download;
        TextView appId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.appIcon);
            appName = itemView.findViewById(R.id.appName);
            appId = itemView.findViewById(R.id.appId);
            download = itemView.findViewById(R.id.download);
        }
    }
}
