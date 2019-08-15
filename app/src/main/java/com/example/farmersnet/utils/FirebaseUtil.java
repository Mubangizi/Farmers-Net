package com.example.farmersnet.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.widget.Toast;

import com.example.farmersnet.R;
import com.example.farmersnet.RegisterActivity;
import com.example.farmersnet.post.Post;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class FirebaseUtil {
    public static FirebaseFirestore firebaseFirestore;
    public static FirebaseAuth mAuth;
    public static FirebaseAuth.AuthStateListener mAuthListener;
    public static ArrayList<Post> postArrayList;
    protected static Activity caller;
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

    @SuppressLint("RestrictedApi")
    public static void resultActivity(int requestCode, int resultCode, Intent data){
            // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
            if (requestCode == RC_SIGN_IN) {
                IdpResponse response = IdpResponse.fromResultIntent(data);
                // Successfully signed in
                if (resultCode == RESULT_OK) {
                    //checking if user is new
                    String userid = mAuth.getUid();
                    firebaseFirestore.collection("Users").document(userid).get().addOnCompleteListener(caller, new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists()){
                            Intent intent = new Intent(caller, RegisterActivity.class);
                            caller.startActivity(intent);
                        }
                        }
                    });

                } else {
                    // Sign in failed
                    if (response == null) {
                        // User pressed back button
                        //showSnackbar(R.string.sign_in_cancelled);
                        return;
                    }

                    if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                        //showSnackbar(R.string.no_internet_connection);
                        return;
                    }

                    Toast.makeText(caller, response.getError().toString(), Toast.LENGTH_SHORT).show();
                    //Log.e(TAG, "Sign-in error: ", response.getError());
                }
            }

    }

}
