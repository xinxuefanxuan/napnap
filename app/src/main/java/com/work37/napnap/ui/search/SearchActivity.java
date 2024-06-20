package com.work37.napnap.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.work37.napnap.Adaptor.GameAdaptor;
import com.work37.napnap.Adaptor.PostAdaptor;
import com.work37.napnap.Adaptor.UserAdaptor;
import com.work37.napnap.R;
import com.work37.napnap.entity.Game;
import com.work37.napnap.entity.Post;
import com.work37.napnap.global.PublicActivity;
import com.work37.napnap.ui.userlogin_register.User;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends PublicActivity {

    private EditText searchInput;
    private Button searchButton;
    private TabLayout searchTab;
    private ViewPager2 searchPager;
    private String[] searchTabs;
    private ImageButton backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchInput = findViewById(R.id.search);
        searchButton = findViewById(R.id.btn_search);
        searchTab = findViewById(R.id.search_tab);
        searchPager = findViewById(R.id.search_pager);
        backButton = findViewById(R.id.btn_back);

        searchTabs = new String[]{
                getString(R.string.games),
                getString(R.string.users),
                getString(R.string.posts)
        };

        searchPager.setAdapter(new FragmentStateAdapter(getSupportFragmentManager(), getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return new GameList1Fragment();
                    case 1:
                        return new UserListFragment();
                    case 2:
                        return new PostListFragment();
                    default:
                        return null;
                }
            }

            @Override
            public int getItemCount() {
                return searchTabs.length;
            }
        });

        new TabLayoutMediator(searchTab, searchPager, (tab, position) -> tab.setText(searchTabs[position])).attach();

        backButton.setOnClickListener(v -> finish());

        searchButton.setOnClickListener(v -> {
            String query = searchInput.getText().toString().trim();
            int currentTabPosition = searchTab.getSelectedTabPosition();
            performSearchInFragment(currentTabPosition, query);
        });

    }

    private void performSearchInFragment(int tabPosition, String query) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + tabPosition);
        if (fragment instanceof GameList1Fragment) {
            ((GameList1Fragment) fragment).performSearch(query);
        } else if (fragment instanceof UserListFragment) {
            ((UserListFragment) fragment).performSearch(query);
        } else if (fragment instanceof PostListFragment) {
            ((PostListFragment) fragment).performSearch(query);
        }
    }
}
