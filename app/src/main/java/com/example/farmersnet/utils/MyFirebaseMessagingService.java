package com.example.farmersnet.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    //TODO to be removed
    private static final String TAG = "MyFirebaseMessagingServ";

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        String notificationBody = "";
        String notificationTitle ="";
        String notificationData = "";
        try {
            notificationData = remoteMessage.getData().toString();
            notificationTitle = remoteMessage.getNotification().getTitle();
            notificationBody = remoteMessage.getNotification().getBody();

        }catch (NullPointerException e){
            Log.e(TAG, "onMessageReceived: NullPointerException " + e.getMessage() );
        }

        Log.d(TAG, "onMessageReceived: data " + notificationData);
        Log.d(TAG, "onMessageReceived: notification body: " + notificationBody);
        Log.d(TAG, "onMessageReceived: notification title: " + notificationTitle);

    }


}
