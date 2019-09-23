package com.example.farmersnet.post;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.farmersnet.R;
import com.example.farmersnet.messages.ChatActivity;
import com.example.farmersnet.messages.Message;
import com.example.farmersnet.utils.FirebaseUtil;
import com.example.farmersnet.utils.MyTimeUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;
    private ArrayList<Post> postList;
    private TextView titleTextView;
    private ImageView postImageView;
    private TextView articleTextView;
    private TextView dateTextView;
    private RecyclerView commentRecyclerView;
    private ArrayList<Message> commentArrayList;
    private CommentRecyclerAdapter commentRecyclerAdapter;
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        postId = getIntent().getExtras().getString("postId");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Post");

        titleTextView = findViewById(R.id.post_rec_title_textView);
        articleTextView = findViewById(R.id.post_rec_article_textView);
        postImageView = findViewById(R.id.post_rec_imageView);
        dateTextView = findViewById(R.id.post_rec_time_textView);
        commentRecyclerView = findViewById(R.id.comment_recyclerView);
        commentArrayList = new ArrayList<Message>();

        FirebaseUtil.openFireBaseReference("Posts", this);
        firebaseFirestore = FirebaseUtil.firebaseFirestore;
        collectionReference = FirebaseUtil.collectionReference;
        postList = FirebaseUtil.postArrayList;

        //Recycler adapter
        commentRecyclerAdapter = new CommentRecyclerAdapter(commentArrayList);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(PostActivity.this, LinearLayoutManager.VERTICAL, false));
        commentRecyclerView.setAdapter(commentRecyclerAdapter);
        getComments();

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

    private void getComments() {

        collectionReference.document(postId).collection("messages").orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (@NonNull DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if(doc.getType()== DocumentChange.Type.ADDED){
                            Message message = doc.getDocument().toObject(Message.class);
                            commentArrayList.add(message);
                            commentRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    private void AddComment(String text, String user_id, String downloadUri){
        final Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("text", text);
        messageMap.put("timestamp", FieldValue.serverTimestamp());
        messageMap.put("user_id", user_id);
        if(downloadUri != null){
            messageMap.put("image", downloadUri);
        }

//        sendMessageIcon.setEnabled(false);
//        collectionReference
//                .add(messageMap)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        messageEditText.setText("");
//                        sendMessageIcon.setEnabled(true);
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        sendMessageIcon.setEnabled(true);
//                        Toast.makeText(ChatActivity.this, "Error: "+ e, Toast.LENGTH_SHORT).show();
//                    }
//                });
    }
}
