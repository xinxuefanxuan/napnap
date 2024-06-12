package com.work37.napnap.global;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PublicActivity extends AppCompatActivity implements BackHandledInterface{
    public BackHandledFragment currentFragment=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(currentFragment == null || !currentFragment.onBackPressed()){
                    //如果没有fragment处理回退时间，调用系统默认的回退逻辑，结束当前activity
                    finish();
                }else{
                    //从返回栈中弹出当前fragment
                    getSupportFragmentManager().popBackStack();
                }
            }
        });
    }

    @Override
    public void setSelectedFragment(BackHandledFragment backHandledFragment) {
        currentFragment=backHandledFragment;
    }

}
