package com.work37.napnap.ui.userlogin_register;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.work37.napnap.R;
import com.work37.napnap.global.GlobalResponse;
import com.work37.napnap.service.ApiServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginLinkEnd {
    private static final String TAG = "LOGIN";
    private MutableLiveData<GlobalResponse> globalResponseMutableLiveData;
    private SharedPreferences preferences;
    private Context context;

    public LoginLinkEnd(Context context,MutableLiveData<GlobalResponse> globalResponseMutableLiveData) {
        this.context = context;
        this.globalResponseMutableLiveData = globalResponseMutableLiveData;
        this.preferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    public void login(String username, String password) {
        String baseUrl = context.getString(R.string.baseurl);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl + "api/user/") // replace with your actual base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiServices apiServices = retrofit.create(ApiServices.class);
        Call<GlobalResponse> call = apiServices.login(new LoginRequest(username, password));

        call.enqueue(new Callback<GlobalResponse>() {
            @Override
            public void onResponse(Call<GlobalResponse> call, Response<GlobalResponse> response) {
                if (response.isSuccessful() && response.body().getData() != null) {
                    globalResponseMutableLiveData.postValue(response.body());
                    JSONObject userData = response.body().getData();
                    try {
                        int id = (int) userData.get("id");
                        String userName = (String) userData.get("userName");
                        String userAvator = (String) userData.get("userAvator");
                        int fanNum = (int) userData.get("fanNum");
                        int focusNum = (int) userData.get("focusNum");
                        String userProfile = (String) userData.get("userProfile");
                        User user = new User(id, userName, fanNum, focusNum, userProfile, userAvator);
                        saveUserToPreferences(user);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        String errorResponse = response.errorBody().string();
                        Log.e(TAG, "Error response: " + errorResponse);
                        GlobalResponse error = new GlobalResponse(response.code(), null, "Error: " + response.code());
                        globalResponseMutableLiveData.postValue(error);
                    } catch (Exception e) {
                        Log.e(TAG, "Exception while parsing error response", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<GlobalResponse> call, Throwable t) {
                Log.e(TAG, "Request failed", t);
                GlobalResponse error = new GlobalResponse(-1, null, t.getMessage());
                globalResponseMutableLiveData.postValue(error);
            }
        });
    }
    private void saveUserToPreferences(User user) {
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user", userJson);
        editor.apply();
    }

    public User getUserFromPreferences() {
        String userJson = preferences.getString("user", null);
        if (userJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }
}

