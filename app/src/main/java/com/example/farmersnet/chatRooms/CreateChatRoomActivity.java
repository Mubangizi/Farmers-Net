package com.example.farmersnet.chatRooms;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.example.farmersnet.RegisterActivity;
import com.example.farmersnet.utils.FirebaseUtil;
import com.example.farmersnet.utils.LoadingScreenUtil;
import com.example.farmersnet.utils.UploadImageUtil;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateChatRoomActivity extends AppCompatActivity {

    private EditText chatNameEditEtext;
    private EditText chatDescEditText;
    private ImageView chatImage;
    private Uri chatImageUri;
    private Button createBtn;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private String TAG;
    private UploadImageUtil uploadImageUtil;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat_room);
        chatNameEditEtext = findViewById(R.id.create_chatroom_name_editText);
        chatDescEditText = findViewById(R.id.create_chatroom_description_editText);
        chatImage = findViewById(R.id.create_chatroom_imageView);
        createBtn = findViewById(R.id.create_chatroom_button);
        LoadingScreenUtil.createscreen("Creating", CreateChatRoomActivity.this);
        uploadImageUtil = new UploadImageUtil(CreateChatRoomActivity.this);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = FirebaseUtil.mAuth.getCurrentUser().getUid();

        chatImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startImageActivity();
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String chatName = chatNameEditEtext.getText().toString();
                final String chatDesc = chatDescEditText.getText().toString();

                if(!TextUtils.isEmpty(chatName) && !TextUtils.isEmpty(chatDesc)){
//                    show loading screen
                    LoadingScreenUtil.dialog.show();
                    FirebaseUtil.openFireBaseReference("ChatRooms", CreateChatRoomActivity.this);
                    firebaseFirestore = FirebaseUtil.firebaseFirestore;
                    collectionReference = FirebaseUtil.collectionReference;

                    if(chatImageUri != null) {
                        String randomName = UUID.randomUUID().toString();

                        final StorageReference imagepath = storageReference.child("chat_room_images/"+randomName+".jpg");
                        UploadTask uploadTask = imagepath.putFile(chatImageUri);

                        final Task<Uri> UriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(CreateChatRoomActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                                }
                                return imagepath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                Uri downloadUri = task.getResult();
                                createChatRoom(chatName, chatDesc, downloadUri.toString());
                            }
                        });
                    }else {
                        createChatRoom(chatName, chatDesc, null);
                    }


                }else {
                    Toast.makeText(CreateChatRoomActivity.this, "Fill Required Fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void createChatRoom(String name, String description, String downloadUri){
        final Map<String, Object> chatRoomMap = new HashMap<>();
        chatRoomMap.put("name", name);
        chatRoomMap.put("description", description);
        chatRoomMap.put("timestamp", FieldValue.serverTimestamp());
        if(downloadUri!=null){
            chatRoomMap.put("image", downloadUri);
        }

        createBtn.setEnabled(false);
        collectionReference
                .add(chatRoomMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        String chatRoomId = documentReference.getId();

                        addchatroomMember(chatRoomId, user_id);
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

    public void startImageActivity(){
        uploadImageUtil.checkPermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uploadImageUtil.setresult(requestCode, resultCode,data, chatImage);
        chatImageUri = uploadImageUtil.getImageUri();
    }

    public void addchatroomMember(String chatRoomId, String user_id){
        String randomName = UUID.randomUUID().toString();
        Map<String, Object> likesMap = new HashMap<>();
        likesMap.put("member", user_id);
        firebaseFirestore.collection("ChatRooms/" + chatRoomId + "/members").add(likesMap);
    }
}
