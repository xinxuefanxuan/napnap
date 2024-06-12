package com.work37.napnap;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.work37.napnap.Adaptor.GameAdaptor;
import com.work37.napnap.entity.Game;

import java.util.ArrayList;
import java.util.List;

public class GameListFragment extends Fragment {

    private static final String ARG_TYPE = "type";
    private String type;

    private RecyclerView recyclerView;
    private GameAdaptor gameAdaptor;
    private List<Game> gameList;

    public static GameListFragment newInstance(String type) {
        GameListFragment fragment = new GameListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        // Initialize game list and adapter
        gameList = new ArrayList<>();
        gameAdaptor = new GameAdaptor(getContext(), gameList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(gameAdaptor);

        // Fetch data from backend based on the type
        fetchGameData(type);

        return view;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void fetchGameData(String type) {
        // Replace with actual data fetching logic based on the type
        // For example, using Retrofit to fetch data from backend

        // Dummy data for illustration
        int rank = 0;
        if (type.equals(getString(R.string.high_recommend))) {
            gameList.add(new Game(++rank,"Recommended Game 1", getResources().getDrawable(R.drawable.napnap,null),"an intesesing game","advanture",100));
            gameList.add(new Game(++rank,"Recommended Game 1", getResources().getDrawable(R.drawable.napnap,null),"an intesesing game","advanture",100));
        } else if (type.equals(getString(R.string.popular_list))) {
            gameList.add(new Game(++rank,"Recommended Game 1", getResources().getDrawable(R.drawable.napnap,null),"an intesesing game","advanture",100));
            gameList.add(new Game(++rank,"Recommended Game 1", getResources().getDrawable(R.drawable.napnap,null),"an intesesing game","advanture",100));
        } else if (type.equals(getString(R.string.highScore_list))) {
            gameList.add(new Game(++rank,"Recommended Game 1", getResources().getDrawable(R.drawable.napnap,null),"an intesesing game","advanture",100));
            gameList.add(new Game(++rank,"Recommended Game 1", getResources().getDrawable(R.drawable.napnap,null),"an intesesing game","advanture",100));
        }

        gameAdaptor.notifyDataSetChanged();
    }
}
