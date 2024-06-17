package com.work37.napnap.ui.userlogin_register;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.work37.napnap.R;
import com.work37.napnap.global.PersistentCookieJar;
import com.work37.napnap.global.PublicApplication;
import com.work37.napnap.global.UrlConstant;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.transform.OutputKeys;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

public class LoginToDataSource {
    private MutableLiveData<LoginReponse> loginReponseMutableLiveData;
//    private OkHttpClient okHttpClient;
    private Context context;

    public LoginToDataSource(MutableLiveData<LoginReponse> loginReponseMutableLiveData,Context context) {
        this.loginReponseMutableLiveData = loginReponseMutableLiveData;
//        this.okHttpClient = new OkHttpClient.Builder()
//                .cookieJar(new PersistentCookieJar());
        this.context = context;
    }
    public void login(String username,String password){
        LoginRequest loginRequest= new LoginRequest(username,password,loginReponseMutableLiveData,context);
        new Thread(loginRequest).start();
    }

    static class LoginRequest implements Runnable{
        static private String userName;
        static private String psw;
        private MutableLiveData<LoginReponse> mutableLiveData;
        private String baseurl;
        private OkHttpClient okHttpClient;

        private Context context;
        public LoginRequest(String username,String password,MutableLiveData<LoginReponse> mutableLiveData,Context context) {
            userName = username;
            psw = password;
            this.mutableLiveData = mutableLiveData;
            this.context = context;
//            this.baseurl = baseurl;
//            this.okHttpClient = okHttpClient;
        }
        @Override
        public void run() {
            try{
                // Create JSON object
                JSONObject jsonObject1 = new JSONObject();
                try {
                    jsonObject1.put("userAccount", userName);
                    jsonObject1.put("userPassword", psw);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                RequestBody requestBody = RequestBody.create(
                        jsonObject1.toString(),
                        MediaType.get("application/json; charset=utf-8")
                );

//                OkHttpClient okHttpClient = new OkHttpClient();
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .cookieJar(new PersistentCookieJar(context))
                        .build();
                Request request = new Request.Builder()
                        .url(UrlConstant.baseUrl+"api/user/login")
                        .post(requestBody)
                        .build();
                Response response = okHttpClient.newCall(request).execute();
                String responseBody = response.body().string();
                Log.d("aaa",responseBody);
                JSONObject jsonObject = new JSONObject(responseBody);
                int code = jsonObject.getInt("code");
                String message = jsonObject.getString("message");
                if(code==0){
                    JSONObject userData = new JSONObject(jsonObject.getString("data"));
                    mutableLiveData.postValue(new LoginReponse(userData));
                }else{
                    mutableLiveData.postValue(new LoginReponse(message));
                }
            }catch(Exception e){
                Log.d("bbb","响应失败");
                mutableLiveData.postValue(new LoginReponse("网络异常"));
                e.printStackTrace();
            }

        }
    }
}
