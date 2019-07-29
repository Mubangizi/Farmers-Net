package com.example.farmersnet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.farmersnet.post.Post;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class PostActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;
    private ArrayList<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        final String postId = getIntent().getExtras().getString("postId");

        FirebaseUtil.openFireBaseReference("Posts");
        firebaseFirestore = FirebaseUtil.firebaseFirestore;
        collectionReference = FirebaseUtil.collectionReference;
        postList = FirebaseUtil.postArrayList;

    }
}
