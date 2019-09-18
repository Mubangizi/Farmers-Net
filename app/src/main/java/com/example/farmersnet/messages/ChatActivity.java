package com.example.farmersnet.messages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.farmersnet.R;
import com.example.farmersnet.utils.FirebaseUtil;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.app.PendingIntent.getActivity;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private ChatRecyclerAdapter chatRecyclerAdapter;
    private ArrayList<Message> messageArrayList;
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatRecyclerView = findViewById(R.id.message_recyclerView);
        messageArrayList = new ArrayList<Message>();
        chatRecyclerAdapter = new ChatRecyclerAdapter(messageArrayList);
        getMessages();
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false));
        chatRecyclerView.setAdapter(chatRecyclerAdapter);

        //firebase
        FirebaseUtil.openFireBaseReference("ref", this);
        collectionReference = FirebaseUtil.collectionReference;

    }

    private void getMessages() {
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (@NonNull DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if(doc.getType()== DocumentChange.Type.ADDED){
//                            String postId = doc.getDocument().getId();
                            Message message = doc.getDocument().toObject(Message.class);
                            messageArrayList.add(message);
                            chatRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }
}
