package com.example.farmersnet.utils;

import com.example.farmersnet.post.Post;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FirebaseUtil {
    public static FirebaseFirestore firebaseFirestore;
    public static ArrayList<Post> postArrayList;
    public static FirebaseUtil firebaseUtil;
    public static CollectionReference collectionReference;

    //to prevent instantiation
    private FirebaseUtil(){}

    public static void openFireBaseReference(String ref){
        if (firebaseUtil == null){
            firebaseUtil = new FirebaseUtil();
            firebaseFirestore = FirebaseFirestore.getInstance();
            postArrayList = new ArrayList<Post>();
        }
        collectionReference = firebaseFirestore.collection(ref);
    }
}
