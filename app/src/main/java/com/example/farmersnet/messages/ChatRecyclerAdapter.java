package com.example.farmersnet.messages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmersnet.R;
import com.example.farmersnet.chatRooms.ChatRoom;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ChatRecyclerViewHolder> {

    public static ArrayList<Message> messageArrayLists;
    Context context;

    public ChatRecyclerAdapter(ArrayList<Message> messageArrayLists){
        this.messageArrayLists= messageArrayLists;

    }


    @NonNull
    @Override
    public ChatRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.chartroom_layout, parent,false);
        return new ChatRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRecyclerViewHolder holder, int position) {
        final Message message = messageArrayLists.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messageArrayLists.size();
    }

    public class ChatRecyclerViewHolder extends RecyclerView.ViewHolder{

        TextView messageTextView;
        ImageView messageImageView;
        TextView messengeTimeTextView;
        TextView messageSenderNameTextView;

        public ChatRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            messageTextView = (TextView) itemView.findViewById(R.id.textView_message_text);
            messageSenderNameTextView = (TextView) itemView.findViewById(R.id.message_sender_name_text);
            messageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);
            messengeTimeTextView = (TextView) itemView.findViewById(R.id.textView_message_time);
        }

        public void bind(Message message){
            messageTextView.setText(message.getText());
            messageSenderNameTextView.setText(message.getName());
            messengeTimeTextView.setText(message.getTimestamp().toString());
        }
    }
}