package com.example.farmersnet.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.farmersnet.R;
import com.example.farmersnet.RegisterActivity;
import com.example.farmersnet.users.User;
import com.example.farmersnet.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AccountFragment extends Fragment {

    private TextView userNameTextView;
    private TextView aboutTextView;
    private TextView contentAboutTextView;
    private TextView interestTextView;
    private TextView emailTextView;
    private TextView dobTextView;
    private ImageView editIcon;
    private ImageView userImageView;

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
        userImageView = view.findViewById(R.id.profile_userimageView);

        FirebaseUtil.openFireBaseReference("Users", getActivity());
        collectionReference = FirebaseUtil.collectionReference;
        mAuth = FirebaseUtil.mAuth;
        try {
            user_id = getActivity().getIntent().getExtras().getString("userId");
        }catch (NullPointerException e){
            user_id = mAuth.getCurrentUser().getUid();
        }

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
        return view;
    }

}
