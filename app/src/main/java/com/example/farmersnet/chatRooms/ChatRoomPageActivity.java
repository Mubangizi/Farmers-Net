package com.example.farmersnet.chatRooms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.farmersnet.R;
import com.example.farmersnet.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;

public class ChatRoomPageActivity extends AppCompatActivity {

    private TextView chatRoomName;
    private TextView chatRoomDescription;
    private ImageView chatRoomImageView;
    private CollectionReference collectionReference;
    private ArrayList<ChatRoom> chatRoomArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_page);
        final String postId = getIntent().getExtras().getString("postId");


        chatRoomName = findViewById(R.id.chatRoomPageNametextView);
        chatRoomDescription = findViewById(R.id.chatRoomPageDescriptiontextView);
        chatRoomImageView = findViewById(R.id.chatRoomPageImageView);

        FirebaseUtil.openFireBaseReference("Posts", this);

        collectionReference = FirebaseUtil.collectionReference;
        chatRoomArrayList = new ArrayList<ChatRoom>();

        collectionReference.document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String name_text = task.getResult().getString("name");
                chatRoomName.setText(name_text);
                String description_text = task.getResult().getString("description");
                chatRoomDescription.setText(description_text);

                String image_uri = task.getResult().getString("image");
                if (image_uri != null) {
                    chatRoomImageView.setAdjustViewBounds(true);
                    Glide.with(ChatRoomPageActivity.this).load(image_uri).into(chatRoomImageView);
                }
            }
        });



    }
}
