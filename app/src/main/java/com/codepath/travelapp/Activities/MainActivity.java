package com.codepath.travelapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.codepath.travelapp.R;
import com.codepath.travelapp.fragments.ComposeFragment;
import com.codepath.travelapp.fragments.ProfileFragment;
import com.codepath.travelapp.fragments.TimelineFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private final String APP_TAG = "MainActivity";
    public static FragmentManager fragmentManager;
    private BottomNavigationView bottomNavigationView;
    private Button logoutBtn;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_home);
        logoutBtn = findViewById(R.id.logoutBtn);
        toolbar = findViewById(R.id.toolbarMain);

        addOnClickListeners();
    }

    private void addOnClickListeners() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = new TimelineFragment();
                        toolbar.setVisibility(View.VISIBLE);
                        logoutBtn.setVisibility(View.INVISIBLE);
                        Log.d(APP_TAG, "Opening timeline fragment");
                        break;
                    case R.id.action_compose:
                        fragment = new ComposeFragment();
                        toolbar.setVisibility(View.GONE);
                        Log.d(APP_TAG, "Opening compose fragment");
                        break;
                    case R.id.action_profile:
                        fragment = new ProfileFragment();
                        Bundle userBundle = new Bundle();
                        userBundle.putString("username",  ParseUser.getCurrentUser().getUsername());
                        fragment.setArguments(userBundle);
                        toolbar.setVisibility(View.VISIBLE);
                        logoutBtn.setVisibility(View.VISIBLE);
                        Log.d(APP_TAG, "Opening profile fragment");

//                        TODO remove is unnecessary
//                        MainActivity.fragmentManager.beginTransaction()
//                                .replace(R.id.flContainer, fragment)
//                                .addToBackStack(null)
//                                .commit();
                        break;
                    default:
                        fragment = new TimelineFragment();
                        Log.d(APP_TAG, "Opening timeline fragment");
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser == null) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                    Log.d(APP_TAG, "User successfully logged out!");
                } else {
                    Log.d(APP_TAG, "Logout failure.");
                }
            }
        });
    }
}
