package com.codepath.travelapp.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.codepath.travelapp.Fragments.AchievementFragment;
import com.codepath.travelapp.Fragments.ComposeFragment;
import com.codepath.travelapp.Fragments.ExploreFragment;
import com.codepath.travelapp.Fragments.ProfileFragment;
import com.codepath.travelapp.Fragments.TimelineFragment;
import com.codepath.travelapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

public class MainActivity extends AppCompatActivity {

    public static final String BACK_STACK_ROOT_TAG = "root_fragment";

    private final String APP_TAG = "MainActivity";
    public static FragmentManager fragmentManager;
    public static BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        addOnClickListeners();
        bottomNavigationView.setSelectedItemId(R.id.action_home);
        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if(isOpen){
                            bottomNavigationView.setVisibility(View.GONE);
                        }else{
                            bottomNavigationView.setVisibility(View.VISIBLE);
                        }
                    }
                });
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
                        break;
                    case R.id.action_compose:
                        fragment = new ComposeFragment();
                        break;
                    case R.id.action_profile:
                        fragment = new ProfileFragment();

                        Bundle userBundle = new Bundle();
                        userBundle.putString("username",  ParseUser.getCurrentUser().getUsername());
                        fragment.setArguments(userBundle);

                        break;
                    case R.id.action_explore:
                        fragment = new ExploreFragment();

                        Bundle bundle = new Bundle();
                        bundle.putString("from", "navBar");
                        fragment.setArguments(bundle);
                        break;
                    case R.id.action_achievements:
                        fragment = new AchievementFragment();
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
            Log.i("MainActivity", "popping back stack");
            fm.popBackStack();
        } else if (fm.getBackStackEntryCount() == 1) { //TODO change order to avoid having an empty if-statement
            // Do Nothing
        } else {
            Log.i("MainActivity", "nothing on back stack, calling super");
            super.onBackPressed();
        }
    }
}
