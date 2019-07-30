package com.codepath.travelapp.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.codepath.travelapp.Fragments.ComposeFragment;
import com.codepath.travelapp.Fragments.ProfileFragment;
import com.codepath.travelapp.Fragments.TimelineFragment;
import com.codepath.travelapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private final String APP_TAG = "MainActivity";
    public static FragmentManager fragmentManager;
    public static BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        toolbar = findViewById(R.id.toolbarMain);
        addOnClickListeners();
        bottomNavigationView.setSelectedItemId(R.id.action_home);
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
                        Log.d(APP_TAG, "Opening profile fragment");
                        MainActivity.fragmentManager.beginTransaction()
                                .replace(R.id.flContainer, fragment)
                                .addToBackStack(null)
                                .commit();
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
    }

    // This disables the back button on the phone
//    @Override
//    public void onBackPressed() {
//        // Disables the back button
//    }
}
