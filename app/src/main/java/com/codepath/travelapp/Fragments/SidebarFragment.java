package com.codepath.travelapp.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.codepath.travelapp.Activities.LoginActivity;
import com.codepath.travelapp.Activities.MainActivity;
import com.codepath.travelapp.Models.User;
import com.codepath.travelapp.OnSwipeTouchListener;
import com.codepath.travelapp.R;
import com.parse.ParseObject;
import com.parse.ParseUser;

import static com.parse.ParseUser.getCurrentUser;

public class SidebarFragment extends Fragment {

    private ParseObject user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sidebar, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvUsername = view.findViewById(R.id.tvUsername);
        Button logoutBtn = view.findViewById(R.id.logout_btn);
        LinearLayout llSidebar = view.findViewById(R.id.llSidebar);
        tvUsername.setText(User.getCurrentUser().getUsername());

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser == null) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                    Log.d("SidebarFragment", "User successfully logged out!");
                } else {
                    Log.d("SidebarFragment", "Logout failure.");
                }
            }
        });

        llSidebar.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            @Override
            public void onSwipeRight() {
                ProfileFragment fragment = new ProfileFragment();
                Bundle userBundle = new Bundle();
                userBundle.putString("username", getCurrentUser().getUsername());
                fragment.setArguments(userBundle);
                MainActivity.fragmentManager.beginTransaction()
                        .replace(R.id.flContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}
