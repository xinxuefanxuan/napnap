package com.work37.napnap.ui.userlogin_register;


import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.work37.napnap.R;
import com.work37.napnap.global.PersistentCookieJar;

import okhttp3.OkHttpClient;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<LoginState> loginStateMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<LoginReponse> loginReponseMutableLiveData = new MutableLiveData<>();
    private LoginToDataSource loginToDataSource;

    private CheckToDataSource checkToDataSource;

    private Context context;
    LoginViewModel(Context context){
        this.context = context;
    }

    public MutableLiveData<LoginState> getLoginStateMutableLiveData() {
        return loginStateMutableLiveData;
    }

    public MutableLiveData<LoginReponse> getLoginReponseMutableLiveData() {
        return loginReponseMutableLiveData;
    }

    public void login(String username,String password){
        this.loginToDataSource = new LoginToDataSource(loginReponseMutableLiveData,context);
        loginToDataSource.login(username,password);
    }

    public void isLogin(){
        this.checkToDataSource = new CheckToDataSource(loginReponseMutableLiveData,context);
        checkToDataSource.isLogin();
    }

    public void LoginDataChange(String username,String password){
        if(!isUserNameValid(username)){
            loginStateMutableLiveData.setValue(new LoginState(R.string.invalid_username,null));
        }else if(!isPasswordValid(password)){
            loginStateMutableLiveData.setValue(new LoginState(null,R.string.invalid_password));
        }else{
            loginStateMutableLiveData.setValue(new LoginState(true));
        }
    }

    public boolean isUserNameValid(String userName){
        if(userName==null){
            return false;
        }
        return !userName.trim().isEmpty();
    }

    public boolean isPasswordValid(String password){
        if(!password.matches("^[a-z0-9]{6,12}$")){
            return false;
        }
        return true;
    }
}
