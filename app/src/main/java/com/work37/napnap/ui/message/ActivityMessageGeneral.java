package com.work37.napnap.ui.message;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.work37.napnap.R;
import com.work37.napnap.ui.message.b_MessageNewFans.FragmentMessageNewFans;
import com.work37.napnap.ui.message.b_MessageNewFav.FragmentMessageNewFav;
import com.work37.napnap.ui.message.b_MessageNewReply.FragmentMessageNewReply;

public class ActivityMessageGeneral extends AppCompatActivity {

    private TabLayout homeTab;
    private ViewPager2 homePager;

    private String[] tabs;
    private String messageClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent =getIntent();
        messageClass = intent.getStringExtra("messageType");

        setContentView(R.layout.activity_message_general);

        homeTab = this.findViewById(R.id.home_tab);
        homePager = this.findViewById(R.id.home_pager);

        tabs = new String[2];
        tabs[0] = new String("未读");
        tabs[1] = new String("已读");

        homePager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (messageClass){
                    case "NewFans":
                        return FragmentMessageNewFans.newInstance(tabs[position]);
                    case "NewLike":
                        return FragmentMessageNewFav.newInstance(tabs[position],"NewLike");
                    case "NewFav":
                        return FragmentMessageNewFav.newInstance(tabs[position],"NewFav");
                    case "NewReply":
                        return FragmentMessageNewReply.newInstance(tabs[position]);
                    default:
                        return FragmentMessageNewFans.newInstance(tabs[position]);
                }
            }
            @Override
            public int getItemCount() {
                return tabs.length;
            }
        });

        new TabLayoutMediator(homeTab, homePager, (tab, position) -> tab.setText(tabs[position])).attach();




    }
}