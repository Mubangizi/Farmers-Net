package com.example.farmersnet.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.farmersnet.R;
import com.example.farmersnet.users.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SearchFragment extends Fragment {

    private ArrayList <User>listUsers;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_usersearch_layout, null);




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

    private void searchUsers(String searchText) {
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

        // Sort the list by date
        Collections.sort(listUsers, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                int res = -1;
                if (o1.getDate() > (o2.getDate())) {
                    res = 1;
                }
                return res;
            }
        });

        userRecyclerAdapter = new UserRecyclerAdapter(listUsers, InvitationActivity.this, this);
        rvUsers.setNestedScrollingEnabled(false);
        rvUsers.setAdapter(userRecyclerAdapter);
        layoutManagerUser = new LinearLayoutManager(getApplicationContext());
        rvUsers.setLayoutManager(layoutManagerUser);
        userRecyclerAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem search = menu.findItem(R.id.action_search);
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

        return true;
    }

}
