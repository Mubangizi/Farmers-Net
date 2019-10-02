package com.example.farmersnet.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private RecyclerView homeRecyclerView;
    private PostRecyclerAdapter postRecyclerAdapter;
    private ArrayList<Post> postArrayList;
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

        FirebaseUtil.openFireBaseReference("Posts", getActivity());
        collectionReference = FirebaseUtil.collectionReference;
        getposts();
        homeRecyclerView = view.findViewById(R.id.home_recyclerView);
        postArrayList = FirebaseUtil.postArrayList;
        postRecyclerAdapter = new PostRecyclerAdapter(postArrayList);
        homeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL, false));
        homeRecyclerView.setAdapter(postRecyclerAdapter);
        FirebaseUtil.attachListener();

        return view;
    }

    public void getposts(){
        collectionReference.orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (@NonNull DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        switch (doc.getType()) {

                            case ADDED:
                                String postId = doc.getDocument().getId();
                                Post post = doc.getDocument().toObject(Post.class).withId(postId);
                                postArrayList.add(post);
                                postRecyclerAdapter.notifyDataSetChanged();
                                break;

                            case REMOVED:
                                //Get the document ID of post in FireStore
                                //Perform a loop and scan the list of announcement to target the correct index
                                for (int i = 0; i < postArrayList.size(); i++) {
                                    //Check if the deleted document ID is equal or exist in the list of announcement
                                    if (doc.getDocument().getId().equals(postArrayList.get(i).PostId)) {
                                        //If yes then delete that object in list by targeting its index
                                        Log.d(TAG, "Removed Post: " + postArrayList.get(i).getTitle());
                                        postArrayList.remove(i);
                                        //Notify the adapter that some item gets remove
                                        postRecyclerAdapter.notifyItemRemoved(i);
                                        //Notify the adapter to update all position
                                        postRecyclerAdapter.notifyItemRangeChanged(i, postArrayList.size());
                                        break;
                                    }
                                }
                                break;

                        }
                    }
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        FirebaseUtil.detachListener();
    }

}
