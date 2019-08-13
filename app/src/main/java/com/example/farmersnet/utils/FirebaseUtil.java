package com.example.farmersnet.utils;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.example.farmersnet.R;
import com.example.farmersnet.post.Post;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {
    public static FirebaseFirestore firebaseFirestore;
    public static FirebaseAuth mAuth;
    public static FirebaseAuth.AuthStateListener mAuthListener;
    public static ArrayList<Post> postArrayList;
    public static Activity caller;
    public static FirebaseUtil firebaseUtil;
    public static CollectionReference collectionReference;
    private static int RC_SIGN_IN =131;

    //to prevent instantiation
    private FirebaseUtil(){}

    public static void openFireBaseReference(String ref, Activity callActivity){
        if (firebaseUtil == null){
            firebaseUtil = new FirebaseUtil();
            firebaseFirestore = FirebaseFirestore.getInstance();
            mAuth =FirebaseAuth.getInstance();
            caller = callActivity;
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (mAuth.getCurrentUser() == null) {
                        FirebaseUtil.signIn();
                    }
                }
            };
            postArrayList = new ArrayList<Post>();
        }
        collectionReference = firebaseFirestore.collection(ref);
    }

    private static void signIn() {
        // Create and launch sign-in intent
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.LoginTheme)
                        .setLogo(R.drawable.farm_logo)
                        .build(),RC_SIGN_IN);
    }

    public static void attachListener(){
        mAuth.addAuthStateListener(mAuthListener);
    }

    public static void detachListener(){
        mAuth.removeAuthStateListener(mAuthListener);
    }
}
