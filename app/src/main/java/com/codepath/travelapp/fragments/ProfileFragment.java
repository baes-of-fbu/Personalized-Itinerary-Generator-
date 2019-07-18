package com.codepath.travelapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.travelapp.Adapters.TripAdapter;
import com.codepath.travelapp.Adapters.UpcomingTripAdapter;
import com.codepath.travelapp.MainActivity;
import com.codepath.travelapp.Models.Trip;
import com.codepath.travelapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL;

public class ProfileFragment extends Fragment {

    private final String TAG = "ProfileFragment";
    private RecyclerView rvUpcoming;
    protected UpcomingTripAdapter adapter;
    protected ArrayList<Trip> mTrips;
    private int pagesize = 10;
    private TextView tvUsername;
    private TextView tvHometown;
    private SwipeRefreshLayout swipeContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvUpcoming = view.findViewById(R.id.rvUpcoming);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvHometown = view.findViewById(R.id.tvHometown);

        //Populate views in Profile Fragment
        tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        //tvHometown.setText((Integer) ParseUser.getCurrentUser().get("hometown"));

        //create the adapter
        mTrips = new ArrayList<>();
        //create the data source
        adapter = new UpcomingTripAdapter(mTrips);
        // set the layout manager on the recycler view
        LinearLayoutManager linerarLayoutManager = new LinearLayoutManager(getContext(), HORIZONTAL, false);
        rvUpcoming.setLayoutManager(linerarLayoutManager);

        rvUpcoming.setAdapter(adapter);
        queryPosts();
        // Swipe Container/ refresh code
//        swipeContainer = view.findViewById(R.id.swipeContainer);
//        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                swipeContainer.setRefreshing(true);
//                //fetchTimelineAsync(); TODO method
//            }
//        });
        // Configure the refreshing colors
//        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);

        RecyclerView.ItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvUpcoming.addItemDecoration(divider);

        Toast.makeText(getContext(), "Welcome to Your Profile",Toast.LENGTH_SHORT).show();
    }
    protected void queryPosts() {

        adapter.clear();

        ParseQuery<Trip> tripQuery = new ParseQuery<Trip>(Trip.class);

        tripQuery.setLimit(pagesize);
        tripQuery.include(Trip.KEY_OWNER);
        tripQuery.addDescendingOrder(Trip.KEY_CREATED_AT);
        tripQuery.findInBackground(new FindCallback<Trip>() {
            @Override
            public void done(List<Trip> trips, ParseException e) {
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
