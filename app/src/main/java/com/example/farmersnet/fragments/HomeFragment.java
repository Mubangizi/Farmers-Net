package com.example.farmersnet.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.farmersnet.R;
import com.example.farmersnet.post.Post;
import com.example.farmersnet.post.PostRecyclerAdapter;
import com.example.farmersnet.utils.FirebaseUtil;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class HomeFragment extends Fragment {
    private RecyclerView homeRecyclerView;
    private PostRecyclerAdapter postRecyclerAdapter;
    private ArrayList<Post> postArrayList;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;
    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        FirebaseUtil.openFireBaseReference("Posts");
        firebaseFirestore = FirebaseUtil.firebaseFirestore;
        collectionReference = FirebaseUtil.collectionReference;
        getposts();
        homeRecyclerView = view.findViewById(R.id.home_recyclerView);
        postArrayList = new ArrayList<>();
        postRecyclerAdapter = new PostRecyclerAdapter(postArrayList);
        homeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        homeRecyclerView.setAdapter(postRecyclerAdapter);
        return view;
    }

    public void getposts(){
        collectionReference.orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (@NonNull DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if(doc.getType()== DocumentChange.Type.ADDED){
                            String postId = doc.getDocument().getId();
                            Post post = doc.getDocument().toObject(Post.class).withId(postId);
                            postArrayList.add(post);
                            postRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

}
