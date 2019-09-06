package com.example.farmersnet.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.farmersnet.R;
import com.example.farmersnet.chatRooms.CreateChatRoomActivity;

public class ChatRoomFragment extends Fragment {

    private FloatingActionButton createChatFab;
    private TextView createChatText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chartroom_layout, container, false);

        createChatFab = view.findViewById(R.id.add_chatroom_fab);
        createChatText = view.findViewById(R.id.chatroom_create_TextView);

        createChatFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTocreateChatRoom();
            }
        });
        createChatText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTocreateChatRoom();
            }
        });
        return view;

    }

    private void sendTocreateChatRoom() {
        Intent createChatIntent = new Intent(getActivity(), CreateChatRoomActivity.class);
        startActivity(createChatIntent);
    }
}
