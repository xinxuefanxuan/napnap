package com.work37.napnap.global;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class PersistentCookieJar implements CookieJar {
    private SharedPreferences sharedPreferences;

    public PersistentCookieJar(Context context) {
        sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (Cookie cookie : cookies) {
            editor.putString(cookie.name(), cookie.toString());
        }
        editor.apply();
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        Map<String, ?> allCookies = sharedPreferences.getAll();
        List<Cookie> result = new ArrayList<>();
        for (String key : allCookies.keySet()) {
            String cookieString = (String) allCookies.get(key);
            Cookie cookie = Cookie.parse(url, cookieString);
            if (cookie != null) {
                result.add(cookie);
            }
        }
        return result;
    }
}