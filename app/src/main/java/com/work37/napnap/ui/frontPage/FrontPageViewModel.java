package com.work37.napnap.ui.frontPage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FrontPageViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public FrontPageViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}