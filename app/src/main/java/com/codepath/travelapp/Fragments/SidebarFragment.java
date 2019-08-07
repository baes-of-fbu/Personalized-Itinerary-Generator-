package com.codepath.travelapp.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.codepath.travelapp.Activities.MainActivity;
import com.codepath.travelapp.Models.User;
import com.codepath.travelapp.OnSwipeTouchListener;
import com.codepath.travelapp.R;

import static com.parse.ParseUser.getCurrentUser;

public class SidebarFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        MainActivity.bottomNavigationView.setVisibility(View.GONE);
        return inflater.inflate(R.layout.fragment_sidebar, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        User user = (User) User.getCurrentUser();
        TextView tvUsername = view.findViewById(R.id.tvFullName);
        Button logoutBtn = view.findViewById(R.id.logout_btn);
        Button editBtn = view.findViewById(R.id.editBtn);
        LinearLayout llSidebar = view.findViewById(R.id.llSidebar);
        tvUsername.setText(user.getFullName());

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = MainActivity.fragmentManager;
                LogoutDialogFragment logoutDialogFragment = LogoutDialogFragment.newInstance();
                logoutDialogFragment.show(fragmentManager, "fragment_logout_confirmation");
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = MainActivity.fragmentManager;
                ProfileDialogFragment profileDialogFragment = ProfileDialogFragment.newInstance();
                profileDialogFragment.show(fragmentManager, "fragment_edit_profile");
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
