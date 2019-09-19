package com.example.farmersnet.utils;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class GetUserNameUtil {
    private static String user_id;
    private static CollectionReference collectionReference;
    private static Activity activity;


    public GetUserNameUtil(String user_id, Activity activity) {
        this.user_id = user_id;
        this.activity = activity;
    }

    public static String getusername(){
        FirebaseUtil.openFireBaseReference("Users", activity);
        collectionReference = FirebaseUtil.collectionReference;
        final String[] username = new String[1];

        collectionReference.document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    String fname = task.getResult().getString("fname");
                    String lname = task.getResult().getString("lname");
                    username[0] = fname+" "+ lname;

                }
            }

        });

        return username[0];
    }
}
