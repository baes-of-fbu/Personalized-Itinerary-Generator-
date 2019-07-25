package com.codepath.travelapp.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.travelapp.Adapters.TripAdapter;
import com.codepath.travelapp.MainActivity;
import com.codepath.travelapp.Models.Trip;
import com.codepath.travelapp.R;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TimelineFragment extends Fragment {

    private final String TAG = "TimelineFragment";

    protected TripAdapter adapter;
    private SwipeRefreshLayout swipeContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView rvPosts = view.findViewById(R.id.rvTrips);

        // create the upcomingTripAdapter
        ArrayList<Trip> mTrips = new ArrayList<>();
        // create the data source
        adapter = new TripAdapter(mTrips);
        // set the layout manager on the recycler view
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPosts.setAdapter(adapter);
        queryPosts();
        // Swipe Container/ refresh code
        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(true);
                queryPosts();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        RecyclerView.ItemDecoration divider = new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL);
        rvPosts.addItemDecoration(divider);

        Toast.makeText(getContext(), "Welcome to Timeline",Toast.LENGTH_SHORT).show();

    }

    private void queryPosts() {
        adapter.clear();
        ParseQuery<Trip> tripQuery = new ParseQuery<>(Trip.class);

        tripQuery.setLimit(10);
        tripQuery.include(Trip.KEY_OWNER);
        tripQuery.addDescendingOrder(Trip.KEY_CREATED_AT);
        tripQuery.findInBackground(new FindCallback<Trip>() {
            @Override
            public void done(List<Trip> trips, ParseException e) {
                swipeContainer.setRefreshing(false);
                if (e != null) {
                    Log.e(TAG,"Error");
                    e.printStackTrace();
                    return;
                }
                adapter.addAll(trips);
            }
        });
    }

}
