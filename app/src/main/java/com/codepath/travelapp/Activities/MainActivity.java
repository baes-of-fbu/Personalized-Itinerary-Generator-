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
import com.codepath.travelapp.Fragments.UserExploreFragment;
import com.codepath.travelapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    public static final String BACK_STACK_ROOT_TAG = "root_fragment";

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
                // Pop off everything up to and including the current tab
                fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = new TimelineFragment();
                        toolbar.setVisibility(View.VISIBLE);
                        break;
                    case R.id.action_compose:
                        fragment = new ComposeFragment();
                        toolbar.setVisibility(View.GONE);
                        break;
                    case R.id.action_profile:
                        fragment = new ProfileFragment();


                        Bundle userBundle = new Bundle();
                        userBundle.putString("username",  ParseUser.getCurrentUser().getUsername());
                        fragment.setArguments(userBundle);


                        toolbar.setVisibility(View.VISIBLE);


                        break;
                    case R.id.action_explore:
                        fragment = new UserExploreFragment();
                        break;
                    default:
                        fragment = new TimelineFragment();
                        Log.d(APP_TAG, "Opening timeline fragment");
                        break;
                }

                fragmentManager.beginTransaction()
                        .replace(R.id.flContainer, fragment, BACK_STACK_ROOT_TAG)
                        .addToBackStack(BACK_STACK_ROOT_TAG)
                        .commit();
                return true;
            }
        });
    }

    @Override
    public void onBackPressed(){
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 1) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();
        } else if (fm.getBackStackEntryCount() == 1) {
            // Do Nothing
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }
}
