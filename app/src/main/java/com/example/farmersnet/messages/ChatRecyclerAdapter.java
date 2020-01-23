package com.example.farmersnet.messages;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmersnet.R;
import com.example.farmersnet.utils.GetUserNameUtil;
import com.example.farmersnet.utils.MyTimeUtil;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Date;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ChatRecyclerViewHolder> {

    public static ArrayList<Message> messageArrayLists;
    Context context;
    FirebaseAuth mAuth;
    String currentUserId;


    public ChatRecyclerAdapter(ArrayList<Message> messageArrayLists){
        this.messageArrayLists= messageArrayLists;
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
    }


    @NonNull
    @Override
    public ChatRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_send_message_layout, parent,false);

        return new ChatRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRecyclerViewHolder holder, int position) {
        final Message message = messageArrayLists.get(position);
        getItemViewType(position);
        holder.bind(message);

        if(message.getUser_id().equals(currentUserId)){
            holder.root_message.setBackgroundResource(R.drawable.rect_round_white);
            holder.userImageView.setVisibility(View.GONE);
            holder.messageSenderNameTextView.setVisibility(View.GONE);
            holder.root_view.setGravity(Gravity.END);
        }


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
        RelativeLayout root_view;
        RelativeLayout root_message;
        ImageView userImageView;

        public ChatRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            messageTextView = itemView.findViewById(R.id.textView_message_text);
            messageSenderNameTextView = itemView.findViewById(R.id.message_sender_name_text);
            messageImageView = itemView.findViewById(R.id.messageImageView);
            messengeTimeTextView = itemView.findViewById(R.id.textView_message_time);
            userImageView = itemView.findViewById(R.id.message_user_image);
            root_message = itemView.findViewById(R.id.message_root);
            root_view = itemView.findViewById(R.id.root_Relative_Layout);
        }

        public void bind(Message message){
            messageTextView.setText(message.getText());
            GetUserNameUtil.setusername(message.getUser_id(), context, messageSenderNameTextView, userImageView);

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
