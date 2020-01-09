package com.example.farmersnet.utils;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.farmersnet.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

public class UserFollowing {
    public static CollectionReference collectionReference;
    public static String currentUserId;
    public static Activity callActivity;

    //to prevent instantiation
    private UserFollowing(){}


    public static void setupUser(Activity callActivity){
        FirebaseUtil.openFireBaseReference("Users", callActivity);
        collectionReference = FirebaseUtil.collectionReference;
        currentUserId = FirebaseUtil.mAuth.getCurrentUser().getUid();
    }

    public static void checkIfFollowing(String userid, final Button followBtn){
        if(currentUserId ==userid){
            followBtn.setVisibility(View.GONE);
        }else {
            collectionReference.document(currentUserId).get().addOnCompleteListener((Activity) callActivity, new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (!task.getResult().exists()) {
                        followBtn.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    public static void followUser(final Button followBtn){
        collectionReference.document(currentUserId).get().addOnCompleteListener((Activity) callActivity, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(!task.getResult().exists()){
                    Map<String, Object> followMap = new HashMap<>();
                    followMap.put("timestamp", FieldValue.serverTimestamp());
                    collectionReference.document(currentUserId).set(followMap);
                    followBtn.setVisibility(View.GONE);
                    Toast.makeText(callActivity, "Followed", Toast.LENGTH_SHORT).show();
                }else {
                    collectionReference.document(currentUserId).delete();
                    Toast.makeText(callActivity, "Unfollowed", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

}
