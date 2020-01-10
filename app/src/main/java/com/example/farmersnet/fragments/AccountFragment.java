package com.example.farmersnet.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.farmersnet.MainActivity;
import com.example.farmersnet.R;
import com.example.farmersnet.RegisterActivity;
import com.example.farmersnet.users.User;
import com.example.farmersnet.utils.FirebaseUtil;
import com.example.farmersnet.utils.GetUserNameUtil;
import com.example.farmersnet.utils.UserFollowing;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.example.farmersnet.utils.UserFollowing.checkIfFollowing;
import static com.example.farmersnet.utils.UserFollowing.followUser;
import static com.example.farmersnet.utils.UserFollowing.setupUser;

public class AccountFragment extends Fragment {

    private TextView userNameTextView;
    private TextView aboutTextView;
    private TextView contentAboutTextView;
    private TextView interestTextView;
    private TextView emailTextView;
    private TextView dobTextView;
    private ImageView editIcon;
    private ImageView userImageView;
    private Button followBtn;
    private TextView followingTextView;
    private TextView boldFollowingTextView;

    private CollectionReference collectionReference;
    private FirebaseAuth mAuth;
    private String user_id;
    private String currentUserId;
    private Context context;

    private RecyclerView usersRecyclerView;
    private LinearLayoutManager linearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_account_layout, null);

        userNameTextView = view.findViewById(R.id.profile_username_textView);
        aboutTextView = view.findViewById(R.id.profile_about_textView);
        interestTextView = view.findViewById(R.id.profile_interest_textView);
        emailTextView = view.findViewById(R.id.profile_emailTextView);
        contentAboutTextView = view.findViewById(R.id.profile_about_content);
        dobTextView = view.findViewById(R.id.profile_dob);
        editIcon = view.findViewById(R.id.profile_edit_icon);
        userImageView = view.findViewById(R.id.profile_userimageView);
        followBtn = view.findViewById(R.id.profile_follow_btn);
        followingTextView = view.findViewById(R.id.following_textView);
        usersRecyclerView = view.findViewById(R.id.users_following_recyclerView);
        boldFollowingTextView = view.findViewById(R.id.bold_following_textView);

        FirebaseUtil.openFireBaseReference("Users", getActivity());
        collectionReference = FirebaseUtil.collectionReference;
        mAuth = FirebaseUtil.mAuth;
        currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        try {
            user_id = getActivity().getIntent().getExtras().getString("userId");
            boldFollowingTextView.setVisibility(View.GONE);
            usersRecyclerView.setVisibility(View.GONE);

        }catch (NullPointerException e){
            user_id = currentUserId;
            boldFollowingTextView.setVisibility(View.VISIBLE);
            usersRecyclerView.setVisibility(View.VISIBLE);
        }

        setupUser(getActivity(), collectionReference.document(currentUserId).collection("following"), followBtn, followingTextView);
        checkIfFollowing(followBtn, user_id);

        collectionReference.document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                        String fname = task.getResult().getString("fname");
                        String lname = task.getResult().getString("lname");
                        String about = task.getResult().getString("about");
                        String interest = task.getResult().getString("interests");
                        String dob = task.getResult().getString("dob");
                        String email = task.getResult().getString("email");
                        String downloadUri = task.getResult().getString("profile_image");
                        userNameTextView.setText(fname + " " + lname );
                        aboutTextView.setText(about);
                        interestTextView.setText(interest);
                        dobTextView.setText(dob);
                        emailTextView.setText(email);
                        contentAboutTextView.setText(about);
                        Glide.with(getContext()).load(downloadUri).into(userImageView);
                }else {
                    FirebaseUtil.attachListener();
                }
            }
        });

        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                followUser();
            }
        });

        followingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserFollowing.unfollowUser();
            }
        });

        getUsers();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            Objects.requireNonNull(getActivity()).getIntent().removeExtra("userId");
        }catch (NullPointerException e){
            user_id = currentUserId;
        }
    }

    //For Recycler View
    public class UserViewHolder extends RecyclerView.ViewHolder{
        private TextView userNameTextView;
        private ImageView userImageView;
        private TextView emailTextView;
        private TextView followingTextView;
        private Button followbtn;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userImageView = itemView.findViewById(R.id.search_user_image);
            userNameTextView = itemView.findViewById(R.id.search_username_textView);
            emailTextView = itemView.findViewById(R.id.search_email);
            followbtn = itemView.findViewById(R.id.list_follow_btn);
            followingTextView = itemView.findViewById(R.id.list_following_textView);

        }

        public void bind(User user){
            String userName = user.getFname()+" "+ user.getLname();
            userNameTextView.setText(userName);
            emailTextView.setText(user.getEmail());

            String imageValue = user.getProfile_image();
            Glide.with(AccountFragment.this).load(imageValue).into(userImageView);

        }

    }

    private void getUsers() {
        Query query = collectionReference.document(currentUserId).collection("following");

        FirestoreRecyclerOptions<User> response = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        FirestoreRecyclerAdapter<User, UserViewHolder> firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<User, UserViewHolder>(response) {
            @Override
            protected void onBindViewHolder(@NonNull final UserViewHolder holder, final int position, @NonNull User model) {

                final String userId = getSnapshots().getSnapshot(position).getId();
                GetUserNameUtil.setusername(userId, context, holder.userNameTextView, holder.userImageView);

//                holder.bind(model);
                holder.userImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendToAccount(userId);
                    }
                });
                holder.userNameTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendToAccount(userId);
                    }
                });

                setupUser(getActivity(), collectionReference.document(currentUserId).collection("following"), holder.followbtn, holder.followingTextView);


                checkIfFollowing(holder.followbtn, userId);

                holder.followbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        followUser();
                    }
                });


                holder.followingTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UserFollowing.unfollowUser();
                    }
                });



            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                context = parent.getContext();
                View view = LayoutInflater.from(context).inflate(R.layout.item_user_search_layout, parent,false);
                return new UserViewHolder(view) ;
            }
        };
        firestoreRecyclerAdapter.startListening();

        usersRecyclerView.setAdapter(firestoreRecyclerAdapter);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        usersRecyclerView.setLayoutManager(linearLayoutManager);
    }


    private void sendToAccount(String userId) {
        Intent accIntent = new Intent(context, MainActivity.class);
        Bundle extras = new Bundle();
        extras.putInt("fragNumber", 3);
        extras.putString("userId", userId);
        accIntent.putExtras(extras);
        startActivity(accIntent);
    }



//    public void checkIfFollowing(String userid, final Button followBtn){
//        if(currentUserId ==userid){
//            followBtn.setVisibility(View.GONE);
//            followingTextView.setVisibility(View.GONE);
//        }else {
//            collectionReference.document(currentUserId).get().addOnCompleteListener((Activity) getContext(), new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if (task.getResult().exists()) {
//                        followBtn.setVisibility(View.GONE);
//                    }
//                }
//            });
//        }
//    }
//
//    public void followUser(){
//        collectionReference.document(currentUserId).get().addOnCompleteListener((Activity) getContext(), new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(!task.getResult().exists()){
//                    Map<String, Object> followMap = new HashMap<>();
//                    followMap.put("timestamp", FieldValue.serverTimestamp());
//                    collectionReference.document(currentUserId).set(followMap);
//                    followBtn.setVisibility(View.GONE);
//                    Toast.makeText(getContext(), "Followed", Toast.LENGTH_SHORT).show();
//                }else {
//                    collectionReference.document(currentUserId).delete();
//                    Toast.makeText(getContext(), "Unfollowed", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });
//    }

}
