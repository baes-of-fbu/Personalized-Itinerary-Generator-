package com.codepath.travelapp.Fragments;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travelapp.Adapters.UserListAdapter;
import com.codepath.travelapp.Models.User;
import com.codepath.travelapp.R;

import java.util.ArrayList;
import java.util.Objects;

public class ListDialogFragment extends DialogFragment {

    private ArrayList<User> users;
    private String listName;

    public ListDialogFragment() {
    }

    public static ListDialogFragment newInstance(ArrayList<User> users, String listName) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("users", users);
        bundle.putString("listName", listName);
        ListDialogFragment fragment = new ListDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            users = bundle.getParcelableArrayList("users");
            listName = bundle.getString("listName");
        }
        return inflater.inflate(R.layout.fragment_list_dialog, container, false);
    }

    public void onResume() {
        // Store access variables for window and blank point
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        Point size = new Point();
        if (window != null) {
            Display display = window.getWindowManager().getDefaultDisplay();
            display.getSize(size);
            // Set the width of the dialog proportional to 75% of the screen width
            window.setLayout((size.x), WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            super.onResume();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvListName = view.findViewById(R.id.tvListName);
        RecyclerView rvUsers = view.findViewById(R.id.rvUsers);

        tvListName.setText(listName);

        UserListAdapter adapter = new UserListAdapter(users);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvUsers.setLayoutManager(linearLayoutManager);
        rvUsers.setAdapter(adapter);
    }
}
