package com.example.farmersnet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.farmersnet.users.User;
import com.example.farmersnet.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.security.AccessController.getContext;

public class SearchActivity extends AppCompatActivity {

    private CollectionReference collectionReference;
    private RecyclerView usersRecView;
    private LinearLayoutManager linearLayoutManager;
    private Context context;
    private String currentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        FirebaseUtil.openFireBaseReference("Users", SearchActivity.this);
        collectionReference = FirebaseUtil.collectionReference;
        currentUserId = FirebaseUtil.mAuth.getCurrentUser().getUid();
        usersRecView = findViewById(R.id.user_search_RecView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getUsers("");
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{
        private TextView userNameTextView;
        private ImageView userImageView;
        private TextView emailTextView;
        private Button followbtn;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userImageView = itemView.findViewById(R.id.search_user_image);
            userNameTextView = itemView.findViewById(R.id.search_username_textView);
            emailTextView = itemView.findViewById(R.id.search_email);
            followbtn = itemView.findViewById(R.id.list_follow_btn);

        }

        public void bind(User user){
            String userName = user.getFname()+" "+ user.getLname();
            userNameTextView.setText(userName);
            emailTextView.setText(user.getEmail());

            String imageValue = user.getProfile_image();
            Glide.with(SearchActivity.this).load(imageValue).into(userImageView);

        }

    }

    private void getUsers(String user_name) {
        Query query = collectionReference.whereEqualTo("lname",user_name);

        FirestoreRecyclerOptions<User> response = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();
        updatelist(response);
    }

    private void updatelist(FirestoreRecyclerOptions<User> response){
        final FirestoreRecyclerAdapter<User, UserViewHolder> firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<User, UserViewHolder>(response) {
            @Override
            protected void onBindViewHolder(@NonNull final UserViewHolder holder, final int position, @NonNull User model) {

                final String userId = getSnapshots().getSnapshot(position).getId();

                holder.bind(model);
                holder.userImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendToAccount(userId);
                    }
                });

                holder.followbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        collectionReference.document(currentUserId).get().addOnCompleteListener((Activity) SearchActivity.this, new OnCompleteListener<DocumentSnapshot>() {

                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(!task.getResult().exists()){
                                    Map<String, Object> followMap = new HashMap<>();
                                    followMap.put("timestamp", FieldValue.serverTimestamp());
                                    collectionReference.document(currentUserId).set(followMap);
                                    holder.followbtn.setVisibility(View.GONE);
                                    Toast.makeText(SearchActivity.this, "Followed", Toast.LENGTH_SHORT).show();
                                }else {
                                    collectionReference.document(currentUserId).delete();
                                    Toast.makeText(SearchActivity.this, "Unfollowed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });



            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                context = parent.getContext();
                View view = LayoutInflater.from(context).inflate(R.layout.item_user_search_layout, parent,false);
                return new UserViewHolder(view) ;
            }
        };
        firestoreRecyclerAdapter.startListening();

        usersRecView.setAdapter(firestoreRecyclerAdapter);
        linearLayoutManager = new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.VERTICAL, false);
        usersRecView.setLayoutManager(linearLayoutManager);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem search = menu.findItem(R.id.action_search_users_item);
        SearchView searchView = (SearchView) search.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                //Toast.makeText(MainActivity.this, "SEARCH " + query, Toast.LENGTH_LONG).show();
                getUsers(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(MainActivity.this, "SEARCH " + newText, Toast.LENGTH_LONG).show();
                getUsers(newText);
                return false;
            }
        });

        return true;
    }

    private void sendToAccount(String userId) {
        Intent accIntent = new Intent(context, MainActivity.class);
        Bundle extras = new Bundle();
        extras.putInt("fragNumber", 3);
        extras.putString("userId", userId);
        accIntent.putExtras(extras);
        startActivity(accIntent);
    }

}
