package com.example.farmersnet.post;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.farmersnet.messages.Likes;
import com.example.farmersnet.utils.FirebaseUtil;
import com.example.farmersnet.R;
import com.example.farmersnet.utils.GetUserNameUtil;
import com.example.farmersnet.utils.MyTimeUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
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

public class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.PostViewHolder> {

    public static ArrayList<Post> postArrayList;
    public FirebaseAuth mAuth;
    public FirebaseFirestore firebaseFirestore;
    public String currentUserId;
    Context context;

    public PostRecyclerAdapter(){
        postArrayList = FirebaseUtil.postArrayList;

    }

    public PostRecyclerAdapter(ArrayList<Post> postArrayList){

        //FirebaseUtil.openFireBaseReference("Posts", ge);
        this.postArrayList = postArrayList;
        firebaseFirestore = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            currentUserId = mAuth.getCurrentUser().getUid();
        }

    }
    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.single_post_layout, viewGroup,false);
        return new PostViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder postViewHolder, int i) {

        final String postId = postArrayList.get(i).PostId;
        final Post post = postArrayList.get(i);
        postViewHolder.bind(post);
        final CollectionReference likesCollectionReference = firebaseFirestore.collection("Posts/" + postId + "/Likes");
        final CollectionReference commentCollectionReference = firebaseFirestore.collection("Posts/" + postId + "/comments");
        final CollectionReference postCollectionReference = firebaseFirestore.collection("Posts");

        postViewHolder.postImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToPost(postId);
            }
        });

        postViewHolder.articleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { sendToPost(postId);
            }
        });

        postViewHolder.postCommentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToPost(postId);
            }
        });

        postViewHolder.optionsButtonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, postViewHolder.optionsButtonTextView);
                //inflating menu from xml resource
                popup.inflate(R.menu.post_item_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete_post:
                                postCollectionReference.document(postId).get().addOnCompleteListener((Activity) context, new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.getResult().exists()){
                                            postCollectionReference.document(currentUserId).delete();
                                            Toast.makeText(context, "Deleted Post", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                                break;
                            case R.id.report:
                                //handle menu2 click
                                break;

                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
            }
        });


        //instantiate likes
        final Likes likes = new Likes(likesCollectionReference, currentUserId, context);
        //ADD A LIKE
        postViewHolder.postLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likes.addALike();
            }
        });
        //GET LIKES
        likes.getLikes(postViewHolder.postLikeBtn);
        //GET LIKES COUNT
        likes.getLikesCount(postViewHolder.postlikeTextView);

        //GET COMMENTS COUNT
        commentCollectionReference.addSnapshotListener((Activity) context, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(!queryDocumentSnapshots.isEmpty()) {
                    int count = queryDocumentSnapshots.size();
                    if(count==1){
                        postViewHolder.postCommentTextView.setText(count+" Comment");
                    }else {
                        postViewHolder.postCommentTextView.setText(count+" Comments");
                    }
                }else{
                    postViewHolder.postCommentTextView.setText("Comment");
                }
            }
        });

    }

    private void sendToPost(String postId) {
        Intent postIntent = new Intent(context, PostActivity.class);
        postIntent.putExtra("postId", postId);
        context.startActivity(postIntent);
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder{
        private TextView titleTextView;
        private TextView articleTextView;
        private TextView dateTextView;
        private TextView userNameTextView;
        private ImageView userImageView;
        private ImageView postImageView;
        private ImageView postLikeBtn;
        private TextView postlikeTextView;
        private TextView postCommentTextView;
        private TextView optionsButtonTextView;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.post_rec_title_textView);
            articleTextView = itemView.findViewById(R.id.post_rec_article_textView);
            dateTextView = itemView.findViewById(R.id.post_rec_time_textView);
            postImageView = itemView.findViewById(R.id.post_rec_imageView);
            userNameTextView = itemView.findViewById(R.id.post_rec_username_textView);
            userImageView = itemView.findViewById(R.id.post_rec_userimageView);
            postLikeBtn = itemView.findViewById(R.id.post_rec_like_icon);
            postlikeTextView = itemView.findViewById(R.id.post_rec_like_textView);
            postCommentTextView = itemView.findViewById(R.id.post_rec_comment_textView);
            optionsButtonTextView = itemView.findViewById(R.id.menu_options_textView);

        }

        public void bind(Post post){
            titleTextView.setText(post.getTitle());
            articleTextView.setText(post.getArticle());
            if(!TextUtils.isEmpty(post.getUser_id())){
                GetUserNameUtil.setusername(post.getUser_id(), context, userNameTextView, userImageView);
            }
            String imageValue = post.getImage();
            if(imageValue == null){
                postImageView.setVisibility(View.GONE);
            }else {
                Glide.with(context).load(imageValue).into(postImageView);
            }

            //POST DATE
            Date timestamp = post.getTimestamp();
            if(timestamp != null){
                Long milliseconds = timestamp.getTime();
                String timeplace = MyTimeUtil.telltime(milliseconds);
                dateTextView.setText(timeplace);
            }else {
                dateTextView.setText("Just now");
            }

        }

    }
}
