package com.example.farmersnet.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmersnet.R;
import com.example.farmersnet.users.User;
import com.example.farmersnet.users.UserRecyclerAdapter;
import com.example.farmersnet.utils.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class SearchFragment extends Fragment {

    private ArrayList <User>listUsers;
    private CollectionReference collectionReference;
    private RecyclerView usersRecView;
    private UserRecyclerAdapter userRecyclerAdapter;
    private LinearLayoutManager linearLayoutManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_usersearch_layout, null);

        FirebaseUtil.openFireBaseReference("Users", getActivity());
        collectionReference = FirebaseUtil.collectionReference;
        return view;
    }


    private void getUsers() {
        collectionReference.whereEqualTo("etat", 1)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            System.err.println("Listen failed:" + e);
                            return;
                        }
                        listUsers = new ArrayList<User>();

                        for (DocumentSnapshot doc : snapshots) {
                            User user = doc.toObject(User.class);
                            listUsers.add(user);
                        }
                        updateListUsers(listUsers);
                    }
                });
    }

    public void searchUsers(String searchText) {
        if (searchText.length() > 0)
            searchText = searchText.substring(0, 1).toUpperCase() + searchText.substring(1).toLowerCase();

        ArrayList<User> results = new ArrayList<>();
        for(User user : listUsers){
            if((user.getFname() != null && user.getFname().contains(searchText)) || (user.getLname() != null && user.getLname().contains(searchText))){
                results.add(user);
            }
        }
        updateListUsers(results);
    }

    private void updateListUsers(ArrayList<User> listUsers) {

        userRecyclerAdapter = new UserRecyclerAdapter(listUsers);
        usersRecView.setNestedScrollingEnabled(false);
        usersRecView.setAdapter(userRecyclerAdapter);
        linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL, false);
        usersRecView.setLayoutManager(linearLayoutManager);
        userRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem search = menu.findItem(R.id.action_search_users);
        SearchView searchView = (SearchView) search.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                //Toast.makeText(MainActivity.this, "SEARCH " + query, Toast.LENGTH_LONG).show();
                searchUsers(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(MainActivity.this, "SEARCH " + newText, Toast.LENGTH_LONG).show();
                searchUsers(newText);
                return false;
            }
        });

    }

}
