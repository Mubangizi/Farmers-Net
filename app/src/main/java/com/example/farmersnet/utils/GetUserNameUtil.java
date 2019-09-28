package com.example.farmersnet.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class GetUserNameUtil {
    private static String user_id;
    private static CollectionReference collectionReference;
    private static Activity activity;


    private GetUserNameUtil() {
    }

    public static void setusername(String user_id, final Context context, final TextView textView, final ImageView userImageView){
        FirebaseUtil.openFireBaseReference("Users", null);
        collectionReference = FirebaseUtil.collectionReference;
        final String[] username = new String[1];

        collectionReference.document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    String fname = task.getResult().getString("fname");
                    String lname = task.getResult().getString("lname");
                    String downloadUri = task.getResult().getString("profile_image");

                    if(textView != null){
                        textView.setText(fname+" "+ lname);
                    }

                    if(userImageView != null)
                        Glide.with(context).load(downloadUri).into(userImageView);
                }
            }

        });
    }
}
