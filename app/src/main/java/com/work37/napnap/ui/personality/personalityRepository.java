package com.work37.napnap.ui.personality;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.work37.napnap.global.PersistentCookieJar;
import com.work37.napnap.global.PublicApplication;
import com.work37.napnap.global.UrlConstant;
import com.work37.napnap.ui.userlogin_register.LoginReponse;
import com.work37.napnap.ui.userlogin_register.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

public class personalityRepository {
    private static volatile personalityRepository instance;

    private final OkHttpClient okHttpClient;
    private Context context;


    private personalityRepository(Context context) {
        this.context = context;
        okHttpClient = new OkHttpClient.Builder()
                .cookieJar(new PersistentCookieJar(context))
                .build();
    }

    public static personalityRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (personalityRepository.class) {
                if (instance == null) {
                    instance = new personalityRepository(context);
                }
            }
        }
        return instance;
    }

    public LiveData<Boolean> logout() throws JSONException, IOException {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(new PersistentCookieJar(context))
                .build();
        Request request = new Request.Builder()
                .url(UrlConstant.baseUrl+"api/user/logout")
                .build();
        Response response = okHttpClient.newCall(request).execute();
        String responseBody = response.body().string();
        Log.d("aaa",responseBody);
        JSONObject jsonObject = new JSONObject(responseBody);
        int code = jsonObject.getInt("code");
        String message = jsonObject.getString("message");
        if(code==0){
            new Handler(Looper.getMainLooper()).post(() -> {
                result.postValue(true);
            });
        }else{
            new Handler(Looper.getMainLooper()).post(() -> {
                result.postValue(false);
            });
        }
        return result;
    }

}
