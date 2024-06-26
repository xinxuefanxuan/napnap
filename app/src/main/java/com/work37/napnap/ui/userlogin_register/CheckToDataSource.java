package com.work37.napnap.ui.userlogin_register;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.work37.napnap.global.PersistentCookieJar;
import com.work37.napnap.global.PublicApplication;
import com.work37.napnap.global.UrlConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Dns;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

public class CheckToDataSource {
    private MutableLiveData<LoginReponse> loginReponseMutableLiveData;
    private Context context;

    public CheckToDataSource(MutableLiveData<LoginReponse> loginReponseMutableLiveData,Context context) {
        this.loginReponseMutableLiveData = loginReponseMutableLiveData;
        this.context = context;
    }
    public void isLogin(){
        isLoginRequest request = new isLoginRequest(loginReponseMutableLiveData, context);
        new Thread(request).start();
    }
    static class isLoginRequest implements Runnable{

        private MutableLiveData<LoginReponse> mutableLiveData;

        private Context context;


        public isLoginRequest(MutableLiveData<LoginReponse> mutableLiveData,Context context) {
            this.mutableLiveData = mutableLiveData;
//            this.baseurl = baseurl;
//            this.okHttpClient = okHttpClient;
            this.context = context;
        }

        @Override
        public void run() {
            try{
                Dns dns = new Dns() {
                    @Override
                    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
                        List<InetAddress> inetAddresses = new ArrayList<>();
                        try {
                            // 使用 Google 的公共 DNS 服务器进行解析
                            InetAddress[] addresses = InetAddress.getAllByName(hostname);
                            for (InetAddress address : addresses) {
                                inetAddresses.add(address);
                            }
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                            // 如果自定义解析失败，使用系统默认的解析方式
                            return Dns.SYSTEM.lookup(hostname);
                        }
                        return inetAddresses;
                    }
                };
                TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            @Override public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                            }
                            @Override public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                            }
                            @Override public X509Certificate[] getAcceptedIssuers() {
                                return new X509Certificate[]{};
                            }
                        } };
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());


                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .cookieJar(new PersistentCookieJar(context))
                        .sslSocketFactory(sslContext.getSocketFactory(),(X509TrustManager) trustAllCerts[0])
                        .hostnameVerifier(((hostname, session) -> true))
                        .protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
                        .dns(dns)
                        .build();
                Request request = new Request.Builder()
                        .url(UrlConstant.baseUrl+"api/user/getLoginUser")
                        .build();
                Response response = okHttpClient.newCall(request).execute();
                String responseBody = response.body().string();
                Log.d("aaa",responseBody);
                JSONObject jsonObject = new JSONObject(responseBody);
                int code = jsonObject.getInt("code");
                String message = jsonObject.getString("message");
                if(code==0){
                    JSONObject data = new JSONObject(jsonObject.getString("data"));
                    User user = null;
                    try {
                        user = new User(data.getLong("id"),data.getString("userName"),data.getLong("fansNum")
                                ,data.getLong("focusNum"),data.getString("userProfile"),data.getString("userAvatar"));
                        user.setUserAccount(data.getString("userAccount"));
                        PublicApplication.setCurrentUser(user);
                        mutableLiveData.postValue(new LoginReponse(data));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
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
