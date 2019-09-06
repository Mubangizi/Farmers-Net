package com.example.farmersnet.chatRooms;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.farmersnet.MainActivity;
import com.example.farmersnet.R;
import com.example.farmersnet.fragments.ChatRoomFragment;
import com.example.farmersnet.utils.FirebaseUtil;
import com.example.farmersnet.utils.LoadingScreenUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateChatRoomActivity extends AppCompatActivity {

    private EditText chatNameEditEtext;
    private EditText chatDescEditText;
    private ImageView chatImage;
    private Button createBtn;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;
    private String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat_room);
        chatNameEditEtext = findViewById(R.id.create_chatroom_name_editText);
        chatDescEditText = findViewById(R.id.create_chatroom_description_editText);
        chatImage = findViewById(R.id.create_chatroom_imageView);
        createBtn = findViewById(R.id.create_chatroom_button);
        LoadingScreenUtil.createscreen("Creating", CreateChatRoomActivity.this);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String chatName = chatNameEditEtext.getText().toString();
                String chatDesc = chatDescEditText.getText().toString();

                if(!TextUtils.isEmpty(chatName) && !TextUtils.isEmpty(chatDesc)){
//                    show loading screen
                    LoadingScreenUtil.dialog.show();
                    FirebaseUtil.openFireBaseReference("ChatRooms", CreateChatRoomActivity.this);
                    firebaseFirestore = FirebaseUtil.firebaseFirestore;
                    collectionReference = FirebaseUtil.collectionReference;
                    createChatRoom(chatName, chatDesc);

                }else {
                    Toast.makeText(CreateChatRoomActivity.this, "Fill Required Fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void createChatRoom(String name, String description){
        final Map<String, Object> chatRoomMap = new HashMap<>();
        chatRoomMap.put("name", name);
        chatRoomMap.put("description", description);
        chatRoomMap.put("timestamp", FieldValue.serverTimestamp());

        createBtn.setEnabled(false);
        collectionReference
                .add(chatRoomMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(CreateChatRoomActivity.this, "Chat room Created", Toast.LENGTH_SHORT).show();
                        LoadingScreenUtil.dialog.dismiss();
                        sendToChatrooms();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        createBtn.setEnabled(true);
                        LoadingScreenUtil.dialog.dismiss();
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }
    private void sendToChatrooms() {
        Intent chatIntent = new Intent(CreateChatRoomActivity.this, MainActivity.class);
        startActivity(chatIntent);
    }
}
