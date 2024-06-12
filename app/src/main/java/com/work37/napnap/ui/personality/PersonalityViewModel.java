package com.work37.napnap.ui.personality;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PersonalityViewModel extends ViewModel {
    private final MutableLiveData<Integer> likeAndCollectsCount = new MutableLiveData<>();
    private final MutableLiveData<Integer> followingCount = new MutableLiveData<>();
    private final MutableLiveData<Integer> followersCount = new MutableLiveData<>();

    public PersonalityViewModel() {
        // 初始化数据，可以从后端获取
        likeAndCollectsCount.setValue(100);
        followingCount.setValue(200);
        followersCount.setValue(300);
    }

    public LiveData<Integer> getLikesandCollectionCount() {
        return likeAndCollectsCount;
    }


    public LiveData<Integer> getFollowingCount() {
        return followingCount;
    }

    public LiveData<Integer> getFollowersCount() {
        return followersCount;
    }
}
