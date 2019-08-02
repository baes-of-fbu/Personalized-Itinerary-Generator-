package com.codepath.travelapp.Fragments;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.codepath.travelapp.Activities.LoginActivity;
import com.codepath.travelapp.Activities.MainActivity;
import com.codepath.travelapp.Activities.OpeningActivity;
import com.codepath.travelapp.R;
import com.parse.ParseUser;

public class LogoutDialogFragment extends DialogFragment {
    private Button okBtn;
    private Button cancelBtn;
    public LogoutDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static LogoutDialogFragment newInstance() {
       LogoutDialogFragment frag = new LogoutDialogFragment();

        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_logout_confirmation, container);
    }

    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        okBtn = view.findViewById(R.id.okBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser == null) {
                    // Pop off everything up to and including the current tab
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.popBackStack(MainActivity.BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    Intent intent = new Intent(getContext(), OpeningActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    Log.d("SidebarFragment", "User successfully logged out!");
                } else {
                    Log.d("SidebarFragment", "Logout failure.");
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
