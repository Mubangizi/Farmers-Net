package com.example.farmersnet.post;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.farmersnet.R;
import com.example.farmersnet.messages.ChatActivity;
import com.example.farmersnet.messages.Likes;
import com.example.farmersnet.messages.Message;
import com.example.farmersnet.utils.FirebaseUtil;
import com.example.farmersnet.utils.GetUserNameUtil;
import com.example.farmersnet.utils.MyTimeUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;
    private FirebaseAuth mAuth;
    private String currentUserId;

    private ArrayList<Post> postList;
    private TextView titleTextView;
    private ImageView postImageView;
    private TextView articleTextView;
    private TextView dateTextView;
    private EditText commentEditText;
    private TextView userNameTextView;
    private ImageView userImageView;
    private ImageView addCommentImageView;
    private ImageView currentUserImageView;
    private ImageView postLikeBtn;
    private TextView postlikeTextView;
    private TextView postCommentTextView;
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
        commentEditText = findViewById(R.id.post_rec_add_comment_editText);
        addCommentImageView = findViewById(R.id.post_rec_add_comment_imageView);
        userNameTextView = findViewById(R.id.post_rec_username_textView);
        userImageView = findViewById(R.id.post_rec_userimageView);
        postLikeBtn = findViewById(R.id.post_rec_like_icon);
        postlikeTextView = findViewById(R.id.post_rec_like_textView);
        postCommentTextView = findViewById(R.id.post_rec_comment_textView);
        currentUserImageView = findViewById(R.id.post_rec_current_user_image);

        commentRecyclerView = findViewById(R.id.comment_recyclerView);
        commentArrayList = new ArrayList<Message>();

        FirebaseUtil.openFireBaseReference("Posts", this);
        firebaseFirestore = FirebaseUtil.firebaseFirestore;
        collectionReference = FirebaseUtil.collectionReference;
        postList = FirebaseUtil.postArrayList;
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        //instantiate likes
        final CollectionReference likescollectionReference = firebaseFirestore.collection("Posts/" + postId + "/Likes");
        final Likes likes = new Likes(likescollectionReference, currentUserId, PostActivity.this);

        //Current User Image
        GetUserNameUtil.setusername(currentUserId, getApplicationContext(), null, currentUserImageView);

        //Recycler adapter
        commentRecyclerAdapter = new CommentRecyclerAdapter(commentArrayList);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(PostActivity.this, LinearLayoutManager.VERTICAL, false));
        commentRecyclerView.setAdapter(commentRecyclerAdapter);
        getComments();

        //get post
        collectionReference.document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String title_text = task.getResult().getString("title");
                titleTextView.setText(title_text);
                String article_text = task.getResult().getString("article");
                String user_id = task.getResult().getString("user_id");
                articleTextView.setText(article_text);

                //user infor
                if(!TextUtils.isEmpty(user_id)){
                    GetUserNameUtil.setusername(user_id, getApplicationContext(), userNameTextView, userImageView);
                }
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
                    postImageView.requestLayout();
                    postImageView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    Glide.with(PostActivity.this).load(image_uri).into(postImageView);
                }
            }
        });


        //ADD A LIKE
        postLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likes.addALike();
            }
        });

        //GET LIKES
        likes.getLikes(postLikeBtn);

        //GET LIKES COUNT
        likes.getLikesCount(postlikeTextView);

        //Add a comment
        addCommentImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String commentText = commentEditText.getText().toString();
            if(!TextUtils.isEmpty(commentText)){
                addComment(commentText, currentUserId);
            }
            }
        });

        setCommentsCount();
    }

    private void getComments() {

        collectionReference.document(postId).collection("comments").orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
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

    private void addComment(String text, String user_id){
        final Map<String, Object> commentMap = new HashMap<>();
        commentMap.put("text", text);
        commentMap.put("timestamp", FieldValue.serverTimestamp());
        commentMap.put("user_id", user_id);

        addCommentImageView.setEnabled(false);
        collectionReference.document(postId).collection("comments")
                .add(commentMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        commentEditText.setText("");
                        addCommentImageView.setEnabled(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        addCommentImageView.setEnabled(true);
                        Toast.makeText(PostActivity.this, "Error: "+ e, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void setCommentsCount(){
        //GET COMMENTS COUNT
        collectionReference.document(postId).collection("comments").addSnapshotListener((Activity) this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(!queryDocumentSnapshots.isEmpty()) {
                    int count = queryDocumentSnapshots.size();
                    if(count==1){
                        postCommentTextView.setText(count+" Comment");
                    }else {
                        postCommentTextView.setText(count+" Comments");
                    }
                }else{
                    postCommentTextView.setText("Comment");
                }
            }
        });
    }
}
