package com.work37.napnap.ui.personality;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EditProfileViewModel extends ViewModel {
    private final MutableLiveData<String> mText;


    public EditProfileViewModel() {
        this.mText = new MutableLiveData<>();
        mText.setValue("This is editprofile fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
