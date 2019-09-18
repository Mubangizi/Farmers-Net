package com.example.farmersnet.chatRooms;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.farmersnet.messages.ChatActivity;
import com.example.farmersnet.R;

import java.util.ArrayList;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder> {

    public static ArrayList<ChatRoom> chatRoomArrayList;
    Context context;

    public ChatRoomAdapter(ArrayList<ChatRoom> chatRoomArrayList){
        this.chatRoomArrayList = chatRoomArrayList;

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
        chatRoomViewHolder.nameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToPost();
            }
        });
        chatRoomViewHolder.roomImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToChatPage();
            }

        });
    }

    @Override
    public int getItemCount() {
        return chatRoomArrayList.size();
    }


    private void sendToChatPage() {
        Intent messageIntent = new Intent(context, ChatRoomPageActivity.class);
        context.startActivity(messageIntent);
    }

    private void sendToPost() {
        Intent messageIntent = new Intent(context, ChatActivity.class);
        context.startActivity(messageIntent);
    }

    public static class ChatRoomViewHolder extends RecyclerView.ViewHolder{
        public  TextView nameTextView;
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
            if( chatRoom.getImage()!=null){
                Glide.with(context).load(chatRoom.getImage()).into(roomImage);
            }
        }
    }
}
