package com.work37.napnap.service;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.work37.napnap.MainActivity;
import com.work37.napnap.R;
import com.work37.napnap.ui.message.ActivityMessageGeneral;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "your_channel_id";
    private static final String TAG = "MyFirebaseMsgService";
    private static final String PREFS_NAME = "FCM_PREFS";
    private static final String TOKEN_KEY = "FCM_TOKEN";


    @Override
    public void onCreate(){
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public void onNewToken(String token){
        Log.d(TAG, "Refreshed token: " + token);
        storeTokenInSharedPreferences(token);
    }

    private void storeTokenInSharedPreferences(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOKEN_KEY, token);
        editor.apply();
        Log.d(TAG, "Token stored in SharedPreferences");
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            String type = remoteMessage.getData().get("type");

            sendNotification(title, body, type);
        }
    }

    private void sendNotification(String title, String body, String type) {
        Intent intent;

        if (type.equals("NewFans")) {
            intent = new Intent(this, ActivityMessageGeneral.class);
            intent.putExtra("messageType", "NewFans");
        } else if(type.equals("NewLike")) {
            intent = new Intent(this, ActivityMessageGeneral.class);
            intent.putExtra("messageType", "NewLike");
        } else if(type.equals("NewFav")) {
            intent = new Intent(this, ActivityMessageGeneral.class);
            intent.putExtra("messageType", "NewFav");
        } else if(type.equals("NewReply")){
            intent = new Intent(this, ActivityMessageGeneral.class);
            intent.putExtra("messageType", "NewReply");
        } else {
            intent = new Intent(this, MainActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT
        );

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.changepassword)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Channel description");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}