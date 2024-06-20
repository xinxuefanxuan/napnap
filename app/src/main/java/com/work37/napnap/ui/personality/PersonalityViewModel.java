package com.work37.napnap.ui.personality;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.work37.napnap.global.PublicApplication;
import com.work37.napnap.ui.userlogin_register.User;

import org.json.JSONException;

public class PersonalityViewModel extends ViewModel {
    private final MutableLiveData<String> username = new MutableLiveData<>();
    private final MutableLiveData<Integer> userAccount = new MutableLiveData<>();
    private final MutableLiveData<Integer> followingCount = new MutableLiveData<>();
    private final MutableLiveData<Integer> followersCount = new MutableLiveData<>();

    private final MutableLiveData<String> userAvatar = new MutableLiveData<>();

    private final personalityRepository personalityRepository;
    private final MutableLiveData<Boolean> logoutResult = new MutableLiveData<>();

    public PersonalityViewModel(personalityRepository personalityRepository) {
        this.personalityRepository = personalityRepository;
        username.postValue("请登录");
        userAccount.postValue(-1);
        followingCount.postValue(-1);
        followersCount.postValue(-1);
    }

    public LiveData<Boolean> getLogoutResult() {
        return logoutResult;
    }

    public void logout() throws JSONException {
        personalityRepository.logout().observeForever(logoutResult::setValue);
    }

    public void updateUser(User user) {
        username.postValue(user.getUsername());
        userAccount.postValue(user.getUid());
        followingCount.postValue(user.getFollowNum());
        followersCount.postValue(user.getFanNum());
        userAvatar.postValue(user.getUserAvatar());
    }

    public LiveData<String> getUsername() {
        return username;
    }

    public LiveData<Integer> getUserAccount() {
        return userAccount;
    }

    public LiveData<Integer> getFollowingCount() {
        return followingCount;
    }

    public LiveData<Integer> getFollowersCount() {
        return followersCount;
    }

    public void setUsername(String name) {
        username.postValue(name);
    }

    public void setUserAccount(int account) {
        userAccount.postValue(account);
    }

    public void setFollowingCount(int count) {
        followingCount.postValue(count);
    }

    public void setFollowersCount(int count) {
        followersCount.postValue(count);
    }

    public void setUserAvatar(String userAvatar){
        this.userAvatar.postValue(userAvatar);
    }

    public MutableLiveData<String> getUserAvatar() {
        return userAvatar;
    }
}

