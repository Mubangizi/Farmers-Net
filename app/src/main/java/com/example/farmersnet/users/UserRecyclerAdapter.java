package com.example.farmersnet.users;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.farmersnet.R;
import com.example.farmersnet.messages.Likes;
import com.example.farmersnet.post.Post;
import com.example.farmersnet.post.PostActivity;
import com.example.farmersnet.utils.FirebaseUtil;
import com.example.farmersnet.utils.GetUserNameUtil;
import com.example.farmersnet.utils.MyTimeUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.UserViewHolder> {

    public static ArrayList<User> userArrayList;
    public FirebaseAuth mAuth;
    public FirebaseFirestore firebaseFirestore;
    public String currentUserId;
    Context context;

    public UserRecyclerAdapter(ArrayList<User> userArrayList){

        //FirebaseUtil.openFireBaseReference("Posts", ge);
        this.userArrayList = userArrayList;
        firebaseFirestore = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            currentUserId = mAuth.getCurrentUser().getUid();
        }

    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.single_post_layout, parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        final User user = userArrayList.get(position);
        holder.bind(user);
    }

//    private void sendToUser(String userId) {
//        Intent userIntent = new Intent(context, PostActivity.class);
//        postIntent.putExtra("postId", postId);
//        context.startActivity(postIntent);
//    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{
        private TextView userNameTextView;
        private ImageView userImageView;
        private TextView emailTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userImageView = itemView.findViewById(R.id.search_user_image);
            userNameTextView = itemView.findViewById(R.id.search_username_textView);
            emailTextView = itemView.findViewById(R.id.search_email);

        }

        public void bind(User user){
            userNameTextView.setText(user.getFname()+" "+ user.getLname());
            emailTextView.setText(user.getEmail());

            String imageValue = user.getProfile_image();
            Glide.with(context).load(imageValue).into(userImageView);

        }

    }
}
