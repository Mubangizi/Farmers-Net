package com.example.farmersnet.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.farmersnet.R;
import com.example.farmersnet.chatRooms.ChatRoom;
import com.example.farmersnet.chatRooms.ChatRoomAdapter;
import com.example.farmersnet.chatRooms.CreateChatRoomActivity;
import com.example.farmersnet.utils.FirebaseUtil;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ChatRoomFragment extends Fragment {

    private FloatingActionButton createChatFab;
    private TextView createChatText;
    private RecyclerView chatRoomsRecyclerView;
    private CollectionReference collectionReference;
    private ArrayList<ChatRoom> chatRoomArrayList;
    private ChatRoomAdapter chatRoomAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chartroom_layout, container, false);

        createChatFab = view.findViewById(R.id.add_chatroom_fab);
        createChatText = view.findViewById(R.id.chatroom_create_TextView);

        chatRoomsRecyclerView = view.findViewById(R.id.chartroom_recyclerView);
        FirebaseUtil.openFireBaseReference("ChatRooms", getActivity());
        collectionReference = FirebaseUtil.collectionReference;
        getChatRooms();
        chatRoomArrayList = new ArrayList<ChatRoom>();
        chatRoomAdapter = new ChatRoomAdapter(chatRoomArrayList);
        chatRoomsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL, false));
        chatRoomsRecyclerView.setAdapter(chatRoomAdapter);

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

    private void getChatRooms() {
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (@NonNull DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if(doc.getType()== DocumentChange.Type.ADDED){
                            String postId = doc.getDocument().getId();
                            ChatRoom chatRoom = doc.getDocument().toObject(ChatRoom.class);
                            chatRoomArrayList.add(chatRoom);
                            chatRoomAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    private void sendTocreateChatRoom() {
        Intent createChatIntent = new Intent(getActivity(), CreateChatRoomActivity.class);
        startActivity(createChatIntent);
    }
}
