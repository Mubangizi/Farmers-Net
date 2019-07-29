package com.example.farmersnet.post;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.farmersnet.utils.FirebaseUtil;
import com.example.farmersnet.PostActivity;
import com.example.farmersnet.R;
import com.example.farmersnet.utils.MyTimeUtil;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

public class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.PostViewHolder> {

    private final FirebaseFirestore firebaseFirestore;
    private final CollectionReference collectionReference;
    public static ArrayList<Post> postArrayList;
    Context context;

    public PostRecyclerAdapter(){

        FirebaseUtil.openFireBaseReference("Posts");
        firebaseFirestore = FirebaseUtil.firebaseFirestore;
        collectionReference = FirebaseUtil.collectionReference;
        postArrayList = FirebaseUtil.postArrayList;

    }

    public PostRecyclerAdapter(ArrayList<Post> postArrayList){

        FirebaseUtil.openFireBaseReference("Posts");
        firebaseFirestore = FirebaseUtil.firebaseFirestore;
        collectionReference = FirebaseUtil.collectionReference;
        this.postArrayList = postArrayList;
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

        postViewHolder.postImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToPost(postId);
            }
        });

        postViewHolder.articleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToPost(postId);
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
        private ImageView postImageView;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.post_rec_title_textView);
            articleTextView = itemView.findViewById(R.id.post_rec_article_textView);
            dateTextView = itemView.findViewById(R.id.post_rec_time_textView);
            postImageView = itemView.findViewById(R.id.post_rec_imageView);

        }

        public void bind(Post post){
            titleTextView.setText(post.getTitle());
            articleTextView.setText(post.getArticle());
            String imageValue = post.getImage();
            if(imageValue == null){
                postImageView.setVisibility(View.GONE);
            }else
                Glide.with(context).load(imageValue).into(postImageView);

            //POST DATE
            Date timestamp = post.getTimestamp();
            if(timestamp != null){
                Long milliseconds = timestamp.getTime();
                String timeplace = MyTimeUtil.telltime(milliseconds);
                dateTextView.setText(timeplace);
            }else {
                dateTextView.setText(null);
            }

        }

    }
}
