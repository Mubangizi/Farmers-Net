package com.example.farmersnet;

import android.content.Intent;
import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.farmersnet.fragments.AccountFragment;
import com.example.farmersnet.fragments.ChatRoomFragment;
import com.example.farmersnet.fragments.CreatePostFragment;
import com.example.farmersnet.fragments.HomeFragment;
import com.example.farmersnet.utils.FirebaseUtil;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigationView = findViewById(R.id.bottomNavView);
        navigationView.setOnNavigationItemSelectedListener(this);

        //loading a fragment
        try{
            int intentFragment = getIntent().getExtras().getInt("fragNumber");
            switch (intentFragment){
                case 2:
                    loadFragment(new ChatRoomFragment());
                    break;
                case 3:
                    loadFragment(new AccountFragment());
                    break;
                default:
                    loadFragment(new HomeFragment());
            }
        }catch (NullPointerException e){
            loadFragment(new HomeFragment());
        }

    }


    private void sendToCreatePost() {
        loadFragment(new CreatePostFragment());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_create_post:
                sendToCreatePost();
                return  true;
            case R.id.action_logout:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                                FirebaseUtil.attachListener();
                            }
                        });
                FirebaseUtil.detachListener();
                return true;
            case R.id.action_search_users:
                Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(searchIntent);
                return true;

            default:
                return true;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        switch (menuItem.getItemId()){
            case R.id.frag_item_home:
                fragment = new HomeFragment();
                break;
            case R.id.frag_item_account:
                fragment = new AccountFragment();
                break;
            case R.id.frag_item_createPost:
                fragment = new CreatePostFragment();
                break;
            case R.id.frag_item_chartRoom:
                fragment = new ChatRoomFragment();
                break;


        }
        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment){
        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_frameLayout, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FirebaseUtil.resultActivity(requestCode, resultCode, data);


    }
}
