package com.example.farmersnet.messages;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmersnet.R;
import com.example.farmersnet.chatRooms.ChatRoom;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.app.PendingIntent.getActivity;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private ChatRecyclerAdapter chatRecyclerAdapter;
    private ArrayList<Message> messageArrayList;
    private CollectionReference collectionReference;
    private FirebaseAuth mAuth;
    private ImageView sendMessageIcon;
    private ImageView addImageButton;
    private ImageView sendImageView;
    private EditText messageEditText;
    private UploadImageUtil uploadImageUtil;
    private StorageReference storageReference;
    private String roomId;
    private Uri imageUri;


    private  final int GALLERY_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        sendMessageIcon = findViewById(R.id.message_send_icon);
        messageEditText = findViewById(R.id.message_text_input);
        addImageButton = findViewById(R.id.message_attachment_icon);
        sendImageView = findViewById(R.id.message_imageView);

        storageReference = FirebaseStorage.getInstance().getReference();

        roomId = getIntent().getExtras().getString("postId");

        uploadImageUtil = new UploadImageUtil(ChatActivity.this);
        LoadingScreenUtil.createscreen("Sending", ChatActivity.this);


        //firebase
        FirebaseUtil.openFireBaseReference("ChatRooms/"+ roomId + "/Messages", this);
        collectionReference = FirebaseUtil.collectionReference;
        mAuth = FirebaseAuth.getInstance();
        final String user_id = mAuth.getCurrentUser().getUid();

        chatRecyclerView = findViewById(R.id.message_recyclerView);
        messageArrayList = new ArrayList<Message>();

        chatRecyclerAdapter = new ChatRecyclerAdapter(messageArrayList);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false));
        chatRecyclerView.setAdapter(chatRecyclerAdapter);
        getMessages();

        //Send Message;
        sendMessageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageText = messageEditText.getText().toString();
                if (!TextUtils.isEmpty(messageText)){
                    sendMessage(messageText, user_id, imageUri);
                }
            }
        });
        //todo: Add sending of images

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImageUtil.checkPermission();
            }
        });


    }

    private void getMessages() {

        collectionReference.orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
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

    private void sendMessage(final String text, final String user_id, Uri imageUri) {

        LoadingScreenUtil.dialog.show();
        if (imageUri != null) {
            String randomName = UUID.randomUUID().toString();
            final StorageReference messageImage = storageReference.child("message_Images/" + roomId + "/" + randomName + ".jpg");

            UploadTask uploadTask = messageImage.putFile(imageUri);

            final Task<Uri> UriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()) {
                        String error = task.getException().getMessage();
                        Toast.makeText(ChatActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                    return messageImage.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUri = task.getResult();
                        savedata(text, user_id, downloadUri.toString());
                }
            });
        } else {
                savedata(text, user_id, null);
        }

    }

    private void savedata(String text, String user_id, String downloadUri){
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
                        messageEditText.setText("");
                        sendMessageIcon.setEnabled(true);
                        LoadingScreenUtil.dialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        sendMessageIcon.setEnabled(true);
                        LoadingScreenUtil.dialog.dismiss();
                        Toast.makeText(ChatActivity.this, "Error: Try Again "+ e, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        uploadImageUtil.setresult(requestCode, resultCode, data, sendImageView);
        imageUri = uploadImageUtil.getImageUri();
    }
}
