package com.work37.napnap.ui.frontPage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.work37.napnap.Adaptor.GameAdaptor;
import com.work37.napnap.R;
import com.work37.napnap.databinding.FragmentFrontpageBinding;
import com.work37.napnap.entity.Game;
import com.work37.napnap.ui.search.SearchActivity;


import java.util.List;

public class FrontPageFragment extends Fragment implements View.OnClickListener{

    private FragmentFrontpageBinding binding;

    private TabLayout homeTab;

    private ViewPager2 homePager;

    private String[] tabs;

    private RecyclerView recyclerView;
    private GameAdaptor gameAdaptor;
    private List<Game> gameList;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFrontpageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        homeTab = root.findViewById(R.id.home_tab);
       // sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        homePager=root.findViewById(R.id.home_pager);

        //设定点击事件
        binding.tlbInit.toolbarSearchInit.setOnClickListener(this);

        //初始化标签数组
        tabs=new String[3];
        tabs[0]=new String(getString(R.string.high_recommend));
        tabs[1]=new String(getString(R.string.popular_list));
        tabs[2]=new String(getString(R.string.highScore_list));

        //创建适配器
        homePager.setAdapter(new FragmentStateAdapter(getChildFragmentManager(),getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return GameListFragment.newInstance(tabs[position]);
            }
            @Override
            public int getItemCount() {
                return tabs.length;
            }
        });

        //连接TabLayout,ViewPager2
        new TabLayoutMediator(homeTab, homePager, (tab, position) -> tab.setText(tabs[position])).attach();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //点击搜索栏之后的响应
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        startActivity(intent);
    }
}