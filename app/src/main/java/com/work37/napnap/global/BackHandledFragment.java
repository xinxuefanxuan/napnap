package com.work37.napnap.global;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

//提供统一的回退事件处理机制给包含他的activity

public abstract class BackHandledFragment extends Fragment {
    protected BackHandledInterface backHandledInterface;
    /**
     * 处理回退事件
     * */
    public abstract boolean onBackPressed();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!(getActivity() instanceof PublicActivity)){
            throw new ClassCastException("Hosting Activity must implement BackHandledInterface");
        }else{
            this.backHandledInterface=(BackHandledInterface) getActivity();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        backHandledInterface.setSelectedFragment(this);
    }
}
