package com.example.farmersnet.post;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmersnet.R;
import com.example.farmersnet.messages.Message;
import com.example.farmersnet.utils.GetUserNameUtil;
import com.example.farmersnet.utils.MyTimeUtil;

import java.util.ArrayList;
import java.util.Date;

public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.CommentViewHolder> {

    public static ArrayList<Message> messageArrayLists;
    Context context;

    public CommentRecyclerAdapter(ArrayList<Message> messageArrayLists){
        this.messageArrayLists= messageArrayLists;

    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment_layout, parent,false);
        return new CommentRecyclerAdapter.CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        final Message message = messageArrayLists.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messageArrayLists.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{

        TextView commentTextView;
        ImageView commentUserImage;
        TextView commentTimeTextView;
        TextView commentUserName;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            commentTextView = (TextView) itemView.findViewById(R.id.comment_textView);
            commentUserName = (TextView) itemView.findViewById(R.id.comment_username_textView);
            commentUserImage = (ImageView) itemView.findViewById(R.id.comment_user_image);
            commentTimeTextView = (TextView) itemView.findViewById(R.id.comment_time_textview);
        }

        public void bind(Message message){
            commentTextView.setText(message.getText());
            GetUserNameUtil.setusername(message.getUser_id(), context, commentUserName, null);

            //Message Time
            Date timestamp = message.getTimestamp();
            if(timestamp != null){
                Long milliseconds = timestamp.getTime();
                String timeplace = MyTimeUtil.telltime(milliseconds);
                commentTimeTextView.setText(timeplace);
            }else {
                commentTimeTextView.setText("Just now");
            }
        }
    }
}
