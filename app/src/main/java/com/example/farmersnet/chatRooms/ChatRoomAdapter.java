package com.example.farmersnet.chatRooms;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.farmersnet.R;
import com.example.farmersnet.post.Post;
import com.example.farmersnet.utils.FirebaseUtil;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder> {

    private final FirebaseFirestore firebaseFirestore;
    private final CollectionReference collectionReference;
    public static ArrayList<ChatRoom> chatRoomArrayList;
    Context context;

    public ChatRoomAdapter(){
        //FirebaseUtil.openFireBaseReference("Posts");
        firebaseFirestore = FirebaseUtil.firebaseFirestore;
        collectionReference = FirebaseUtil.collectionReference;
        chatRoomArrayList = new ArrayList<ChatRoom>();

    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.chartroom_layout, viewGroup,false);
        return new ChatRoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomViewHolder chatRoomViewHolder, int i) {

        final ChatRoom chatRoom = chatRoomArrayList.get(i);
        chatRoomViewHolder.bind(chatRoom);
    }

    @Override
    public int getItemCount() {
        return chatRoomArrayList.size();
    }

    public class ChatRoomViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTextView;
        private TextView descTextVew;
        private ImageView roomImage;
        public ChatRoomViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.chartroom_title_textView);
            descTextVew = itemView.findViewById(R.id.Chartroom_description_textViews);
            roomImage = itemView.findViewById(R.id.chartroom_imageView);
        }

        public void bind(ChatRoom chatRoom){
            nameTextView.setText(chatRoom.getName());
            descTextVew.setText(chatRoom.getDescription());
        }

    }
}
