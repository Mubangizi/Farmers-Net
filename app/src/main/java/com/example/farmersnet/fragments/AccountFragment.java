package com.example.farmersnet.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.farmersnet.R;
import com.example.farmersnet.RegisterActivity;
import com.example.farmersnet.post.Post;
import com.example.farmersnet.post.PostRecyclerAdapter;
import com.example.farmersnet.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AccountFragment extends Fragment {

    private TextView userNameTextView;
    private TextView aboutTextView;
    private TextView contentAboutTextView;
    private TextView interestTextView;
    private TextView emailTextView;
    private TextView dobTextView;
    private ImageView editIcon;

    private CollectionReference collectionReference;
    private FirebaseAuth mAuth;
    private String user_id;
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

        FirebaseUtil.openFireBaseReference("Users", getActivity());
        collectionReference = FirebaseUtil.collectionReference;
        mAuth = FirebaseUtil.mAuth;
        user_id = mAuth.getCurrentUser().getUid();

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
                        userNameTextView.setText(fname + " " + lname );
                        aboutTextView.setText(about);
                        interestTextView.setText(interest);
                        dobTextView.setText(dob);
                        emailTextView.setText(email);
                        contentAboutTextView.setText(about);
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
        return view;
    }
}
