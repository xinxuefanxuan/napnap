package com.work37.napnap.ui.message;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MessageViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public MessageViewModel() {
        this.mText = new MutableLiveData<>();
        mText.setValue("This is message fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
