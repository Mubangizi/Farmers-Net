package com.example.farmersnet.messages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.farmersnet.R;

import java.util.ArrayList;

import static android.app.PendingIntent.getActivity;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private ChatRecyclerAdapter chatRecyclerAdapter;
    private ArrayList<Message> messageArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageArrayList = new ArrayList<Message>();
        chatRecyclerAdapter = new ChatRecyclerAdapter(messageArrayList);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false));
        chatRecyclerView.setAdapter(chatRecyclerAdapter);

    }
}
