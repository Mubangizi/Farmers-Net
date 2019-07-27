package com.example.farmersnet.post;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.farmersnet.FirebaseUtil;
import com.example.farmersnet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.PostViewHolder> {

    private final FirebaseFirestore firebaseFirestore;
    private final CollectionReference collectionReference;
    public static ArrayList<Post> postArrayList;

    public PostRecyclerAdapter(){

        FirebaseUtil.openFireBaseReference("Posts");
        firebaseFirestore = FirebaseUtil.firebaseFirestore;
        collectionReference = FirebaseUtil.collectionReference;
        postArrayList = FirebaseUtil.postArrayList;

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                    Post post = documentSnapshot.toObject(Post.class);
                    postArrayList.add(post);
                }
            }
        });
    }
    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.single_post_layout, viewGroup,false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder postViewHolder, int i) {

        Post post = postArrayList.get(i);
        postViewHolder.bind(post);

    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder{
        private TextView titleTextView;
        private TextView articleextView;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.post_rec_title_textView);
            articleextView = itemView.findViewById(R.id.post_rec_article_textView);
        }

        public void bind(Post post){
            titleTextView.setText(post.getTitle());
            articleextView.setText(post.getArticle());
        }
    }
}
