package com.example.farmersnet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.farmersnet.users.User;
import com.example.farmersnet.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private CollectionReference collectionReference;
    private RecyclerView usersRecView;
    private LinearLayoutManager linearLayoutManager;
    private Context context;
    private ArrayList<User> userArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        FirebaseUtil.openFireBaseReference("Users", SearchActivity.this);
        collectionReference = FirebaseUtil.collectionReference;
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

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userImageView = itemView.findViewById(R.id.search_user_image);
            userNameTextView = itemView.findViewById(R.id.search_username_textView);
            emailTextView = itemView.findViewById(R.id.search_email);

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
        FirestoreRecyclerAdapter<User, UserViewHolder> firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<User, UserViewHolder>(response) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {

                holder.bind(model);
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

}
