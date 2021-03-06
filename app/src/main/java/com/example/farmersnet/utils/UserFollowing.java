package com.example.farmersnet.utils;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.farmersnet.R;
import com.example.farmersnet.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class UserFollowing {
    public static CollectionReference collectionReference;
    public static String currentUserId;
    public static Activity callActivity;
    public static String userId;
    public static Button followBtn;
    public static TextView followingTextview;

    //to prevent instantiation
    private UserFollowing(){}


    public static void setupUser(Activity calledActivity, CollectionReference collectionRef, Button mfollowBtn, TextView mfollowingTextview){
        FirebaseUtil.openFireBaseReference("Users", calledActivity);
        collectionReference = collectionRef;
        currentUserId = FirebaseUtil.mAuth.getCurrentUser().getUid();
        callActivity=calledActivity;
        followBtn = mfollowBtn;
        followingTextview = mfollowingTextview;
    }

    public static void checkIfFollowing(final Button followBtn, String userIds){
        userId = userIds;
        if(currentUserId ==userId){
            followBtn.setVisibility(View.GONE);
        }else {
            collectionReference.document(userId).addSnapshotListener((Activity) callActivity, new EventListener<DocumentSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    if(documentSnapshot.exists()){

                        followBtn.setVisibility(View.GONE);
                    }else {
                        followBtn.setVisibility(View.VISIBLE);
                    }
                }

            });
        }
    }

    public static void followUser(){
        collectionReference.document(currentUserId).get().addOnCompleteListener((Activity) callActivity, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(!task.getResult().exists()){
                    Map<String, Object> followMap = new HashMap<>();
                    followMap.put("timestamp", FieldValue.serverTimestamp());
                    collectionReference.document(userId).set(followMap);
                    followBtn.setVisibility(View.GONE);
                    followingTextview.setVisibility(View.VISIBLE);
                    Toast.makeText(callActivity, "Followed", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(callActivity, "Following user", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public static void unfollowUser(){
        collectionReference.document(currentUserId).get().addOnCompleteListener((Activity) callActivity, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(!task.getResult().exists()){
                    collectionReference.document(userId).delete();
                    Toast.makeText(callActivity, "Unfollowed", Toast.LENGTH_SHORT).show();
                    followingTextview.setVisibility(View.INVISIBLE);
                    followBtn.setVisibility(View.VISIBLE);
                }else {
                    Toast.makeText(callActivity, "Error occured", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


}
