package com.example.farmersnet.messages;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.farmersnet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class Likes {

    private CollectionReference collectionReference;
    private String currentUserId;
    private Context context;

     public Likes(CollectionReference collectionReference, String currentUserId, Context context){
        this.collectionReference = collectionReference;
        this.currentUserId = currentUserId;
        this.context = context;
    }

    public void addALike(){
        collectionReference.document(currentUserId).get().addOnCompleteListener((Activity) context, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(!task.getResult().exists()){
                    Map<String, Object> likesMap = new HashMap<>();
                    likesMap.put("timestamp", FieldValue.serverTimestamp());
                    collectionReference.document(currentUserId).set(likesMap);
                    Toast.makeText(context, "Liked Post", Toast.LENGTH_SHORT).show();
                }else {
                    collectionReference.document(currentUserId).delete();
                    Toast.makeText(context, "Unliked Post", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void getLikes(final ImageView imageView){
        //GET LIKES
        collectionReference.document(currentUserId).addSnapshotListener((Activity) context, new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){

                    imageView.setImageDrawable(context.getDrawable(R.mipmap.ic_like_purple_icon));
                }else {
                    imageView.setImageDrawable(context.getDrawable(R.mipmap.ic_like_dark_icon));
                }
            }

        });
    }
    public void getLikesCount(final TextView textView){
        //GET LIKES COUNT

        collectionReference.addSnapshotListener((Activity) context, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(!queryDocumentSnapshots.isEmpty()) {
                    int count = queryDocumentSnapshots.size();
                    textView.setText(count+" Likes");
                }else{
                    textView.setText("Likes");
                }
            }
        });
    }
}
