package com.example.farmersnet;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.PointerIcon;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.farmersnet.post.Post;
import com.example.farmersnet.post.PostRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.AbstractSequentialList;
import java.util.ArrayList;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {
    private RecyclerView postRecyclerView;
    private ArrayList<Post> posts;
    private TextView titletextview;
    private RecyclerView mainRecyclerView;
    private PostRecyclerAdapter postRecyclerAdapter;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;
    private ArrayList<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titletextview = findViewById(R.id.title_textView);
        FirebaseUtil.openFireBaseReference("Posts");
        firebaseFirestore = FirebaseUtil.firebaseFirestore;
        collectionReference = FirebaseUtil.collectionReference;
        mainRecyclerView = findViewById(R.id.main_recylerView);
        postList = FirebaseUtil.postArrayList;
        getposts();
        postRecyclerAdapter = new PostRecyclerAdapter(postList);
        setAdapter();

    }

    private void setAdapter() {
        mainRecyclerView.setAdapter(postRecyclerAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mainRecyclerView.setLayoutManager(linearLayoutManager);
        mainRecyclerView.setAdapter(postRecyclerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAdapter();

    }

    private void sendToMain() {
        Intent createIntent = new Intent(MainActivity.this, CreatePostActivity.class);
        startActivity(createIntent);
    }

    public void getposts(){
        collectionReference.orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                if (!queryDocumentSnapshots.isEmpty()) {
                    for (@NonNull DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if(doc.getType()== DocumentChange.Type.ADDED){
                            String postId = doc.getDocument().getId();
                            Post post = doc.getDocument().toObject(Post.class).withId(postId);
                            postList.add(post);
                            postRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_create_post:
                sendToMain();
                return  true;

            default:
                return true;
        }
    }

}
