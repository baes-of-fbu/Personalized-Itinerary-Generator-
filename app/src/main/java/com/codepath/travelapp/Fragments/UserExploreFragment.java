package com.codepath.travelapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travelapp.Adapters.UserAdapter;
import com.codepath.travelapp.Models.User;
import com.codepath.travelapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class UserExploreFragment extends Fragment {

    protected UserAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView rvUsers = view.findViewById(R.id.rvUsers);
        SearchView svUsers = view.findViewById(R.id.svUsers);
        ArrayList<User> mUsers = new ArrayList<>();
        adapter = new UserAdapter(mUsers);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvUsers.setLayoutManager(linearLayoutManager);
        rvUsers.setAdapter(adapter);

        queryUsers(null);
        svUsers.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String keyword) {
                queryUsers(keyword);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String keyword) {
                queryUsers(keyword);
                return true;
            }
        });

    }

    private void queryUsers(String keyword) {
        adapter.clear();
        ParseQuery<User> userQuery = new ParseQuery<>(User.class);
        if( keyword != null) {
            //userQuery.whereContains("username", keyword);
            userQuery.whereContains("fullName" , keyword);
        }
        userQuery.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> users, ParseException e) {
                if (e == null) {
                    adapter.addAll(users);
                }else{
                    e.printStackTrace();
                }
            }
        });
    }
}
