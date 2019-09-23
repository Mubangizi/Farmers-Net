package com.example.farmersnet.post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.farmersnet.R;
import com.example.farmersnet.utils.FirebaseUtil;
import com.example.farmersnet.utils.MyTimeUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

public class PostActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;
    private ArrayList<Post> postList;
    private TextView titleTextView;
    private ImageView postImageView;
    private TextView articleTextView;
    private TextView dateTextView;
    private RecyclerView commentRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        final String postId = getIntent().getExtras().getString("postId");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Post");

        titleTextView = findViewById(R.id.post_rec_title_textView);
        articleTextView = findViewById(R.id.post_rec_article_textView);
        postImageView = findViewById(R.id.post_rec_imageView);
        dateTextView = findViewById(R.id.post_rec_time_textView);
        commentRecyclerView = findViewById(R.id.comment_recyclerView);

        FirebaseUtil.openFireBaseReference("Posts", this);
        firebaseFirestore = FirebaseUtil.firebaseFirestore;
        collectionReference = FirebaseUtil.collectionReference;
        postList = FirebaseUtil.postArrayList;

        collectionReference.document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String title_text = task.getResult().getString("title");
                titleTextView.setText(title_text);
                String article_text = task.getResult().getString("article");
                articleTextView.setText(article_text);

                Date timestamp = task.getResult().getDate("timestamp");
                if(timestamp != null){
                    Long milliseconds = timestamp.getTime();
                    String timeplace = MyTimeUtil.telltime(milliseconds);
                    dateTextView.setText(timeplace);
                }else {
                    dateTextView.setText(null);
                }

                String image_uri = task.getResult().getString("image");
                if(image_uri == null){
                    postImageView.setVisibility(View.GONE);
                }else {
                    postImageView.setAdjustViewBounds(true);
                    Glide.with(PostActivity.this).load(image_uri).into(postImageView);
                }
            }
        });

    }
}
