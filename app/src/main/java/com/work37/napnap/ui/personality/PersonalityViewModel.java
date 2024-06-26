package com.work37.napnap.ui.personality;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.work37.napnap.global.PublicApplication;
import com.work37.napnap.ui.userlogin_register.User;

import org.json.JSONException;

import java.io.IOException;

public class PersonalityViewModel extends ViewModel {
    private final MutableLiveData<String> username = new MutableLiveData<>();
    private final MutableLiveData<String> userAccount = new MutableLiveData<>();
    private final MutableLiveData<Long> followingCount = new MutableLiveData<>();
    private final MutableLiveData<Long> followersCount = new MutableLiveData<>();

    private final MutableLiveData<String> userAvatar = new MutableLiveData<>();

    private final personalityRepository personalityRepository;

    public PersonalityViewModel(personalityRepository personalityRepository) {
        this.personalityRepository = personalityRepository;
        username.postValue("请登录");
        userAccount.postValue("");
        followingCount.postValue(new Long(-1));
        followersCount.postValue(new Long(-1));
    }



    public void updateUser(User user) {
        username.postValue(user.getUserName());
        userAccount.postValue(user.getUserAccount());
        followingCount.postValue(user.getFollowNum());
        followersCount.postValue(user.getFanNum());
        userAvatar.postValue(user.getUserAvatar());
    }

    public LiveData<String> getUsername() {
        return username;
    }

    public LiveData<String> getUserAccount() {
        return userAccount;
    }

    public LiveData<Long> getFollowingCount() {
        return followingCount;
    }

    public LiveData<Long> getFollowersCount() {
        return followersCount;
    }

    public void setUsername(String name) {
        username.postValue(name);
    }

    public void setUserAccount(String account) {
        userAccount.postValue(account);
    }

    public void setFollowingCount(Long count) {
        followingCount.postValue(count);
    }

    public void setFollowersCount(Long count) {
        followersCount.postValue(count);
    }

    public void setUserAvatar(String userAvatar){
        this.userAvatar.postValue(userAvatar);
    }

    public MutableLiveData<String> getUserAvatar() {
        return userAvatar;
    }
}

