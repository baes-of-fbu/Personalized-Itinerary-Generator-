package com.codepath.travelapp;

import android.content.Intent;
import android.media.MediaMetadataEditor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.codepath.travelapp.fragments.ComposeFragment;
import com.codepath.travelapp.fragments.ProfileFragment;
import com.codepath.travelapp.fragments.TimelineFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    public static FragmentManager fragmentManager;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        final Button logoutBtn = findViewById(R.id.logoutBtn);
        final Toolbar toolbar = findViewById(R.id.toolbarMain);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = new TimelineFragment();
                        //toolbar.setVisibility(View.VISIBLE);
                        //logoutBtn.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainActivity.this, "Home!", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_compose:
                        fragment = new ComposeFragment();
                        toolbar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Compose!", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_profile:
                        fragment = new ProfileFragment();
                        Bundle userBundle = new Bundle();
                        userBundle.putString("username",  ParseUser.getCurrentUser().getUsername());
                        fragment.setArguments(userBundle);

                        MainActivity.fragmentManager.beginTransaction()
                                .replace(R.id.flContainer, fragment)
                                .addToBackStack(null)
                                .commit();
                        toolbar.setVisibility(View.VISIBLE);
                        logoutBtn.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this, "Profile!", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        fragment = new TimelineFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_home);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser == null) {
                    Log.d(TAG, "User successfully logged out!");
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d(TAG, "Logout failure.");
                }
            }
        });

    }
}
