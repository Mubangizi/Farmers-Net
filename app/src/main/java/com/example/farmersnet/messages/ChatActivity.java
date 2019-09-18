package com.example.farmersnet.messages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmersnet.R;
import com.example.farmersnet.chatRooms.ChatRoom;
import com.example.farmersnet.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.PendingIntent.getActivity;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private ChatRecyclerAdapter chatRecyclerAdapter;
    private ArrayList<Message> messageArrayList;
    private CollectionReference collectionReference;
    private FirebaseAuth mAuth;
    private ImageView sendMessageIcon;
    private EditText messageEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        sendMessageIcon = findViewById(R.id.message_send_icon);
        messageEditText = findViewById(R.id.message_text_input);
        final String roomId = getIntent().getExtras().getString("postId");

        chatRecyclerView = findViewById(R.id.message_recyclerView);
        messageArrayList = new ArrayList<Message>();
        chatRecyclerAdapter = new ChatRecyclerAdapter(messageArrayList);
        getMessages();
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false));
        chatRecyclerView.setAdapter(chatRecyclerAdapter);

        //firebase
        FirebaseUtil.openFireBaseReference("ChatRooms/"+ roomId + "/Messages", this);
        collectionReference = FirebaseUtil.collectionReference;
        mAuth = FirebaseAuth.getInstance();
        final String user_id = mAuth.getCurrentUser().getUid();

        //Send Message;
        sendMessageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageText = messageEditText.getText().toString();
                sendMessage(messageText, user_id, null);
            }
        });

    }

    private void getMessages() {
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (@NonNull DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if(doc.getType()== DocumentChange.Type.ADDED){
                            Message message = doc.getDocument().toObject(Message.class);
                            messageArrayList.add(message);
                            chatRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    private void sendMessage(String text, String user_id, String downloadUri){
        final Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("text", text);
        messageMap.put("timestamp", FieldValue.serverTimestamp());
        messageMap.put("user_id", user_id);
        if(downloadUri != null){
            messageMap.put("image", downloadUri);
        }

        sendMessageIcon.setEnabled(false);
        collectionReference
                .add(messageMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(ChatActivity.this, "Post Created", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        sendMessageIcon.setEnabled(true);
                        Toast.makeText(ChatActivity.this, "Error adding document: "+ e, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
