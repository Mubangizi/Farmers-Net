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
import com.example.farmersnet.utils.GetUserNameUtil;
import com.example.farmersnet.utils.MyTimeUtil;

import java.util.ArrayList;
import java.util.Date;

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
        View view = LayoutInflater.from(context).inflate(R.layout.item_message_layout, parent,false);
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
            GetUserNameUtil.setusername(message.getUser_id(), context, messageSenderNameTextView, null);

            //Message Time
            Date timestamp = message.getTimestamp();
            if(timestamp != null){
                Long milliseconds = timestamp.getTime();
                String timeplace = MyTimeUtil.telltime(milliseconds);
                messengeTimeTextView.setText(timeplace);
            }else {
                messengeTimeTextView.setText("Just now");
            }
        }
    }
}
