package com.example.farmersnet;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.farmersnet.fragments.AccountFragment;
import com.example.farmersnet.fragments.ChartRoomFragment;
import com.example.farmersnet.fragments.CreatePostFragment;
import com.example.farmersnet.fragments.HomeFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigationView = findViewById(R.id.bottomNavView);
        navigationView.setOnNavigationItemSelectedListener(this);
        loadFragment(new HomeFragment());

    }


    private void sendToCreatePost() {
        Intent createIntent = new Intent(MainActivity.this, CreatePostActivity.class);
        startActivity(createIntent);
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
                fragment = new ChartRoomFragment();
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
}
