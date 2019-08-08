package com.codepath.travelapp.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.travelapp.Activities.MainActivity;
import com.codepath.travelapp.Adapters.TimelineAdapter;
import com.codepath.travelapp.EndlessRecyclerViewScrollListener;
import com.codepath.travelapp.Models.Trip;
import com.codepath.travelapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TimelineFragment extends Fragment {

    private final int page_size = 10;
    private static int PIXEL_OFFSET = 200;

    private TimelineAdapter timelineAdapter;
    private SwipeRefreshLayout swipeContainer;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView rvTrips;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading your timeline...");
        progressDialog.setTitle("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Creates a timeline timelineAdapter with an empty array list of trips
        ArrayList<Trip> mTrips = new ArrayList<>();
        timelineAdapter = new TimelineAdapter(mTrips);

        rvTrips = view.findViewById(R.id.rvTrips);
        swipeContainer = view.findViewById(R.id.swipeContainer);

        // Connects adapter with recycler view
        linearLayoutManager = new LinearLayoutManager(getContext());
        rvTrips.setLayoutManager(linearLayoutManager);
        rvTrips.setAdapter(timelineAdapter);

        // Adds endless scroll and scroll to refresh listeners
        addScrollListeners(linearLayoutManager);

        // Queries posts from parse network
        queryFollowingPosts(0);
    }

    private void addScrollListeners(LinearLayoutManager linearLayoutManager) {
        // Adds endless scroll listener
        EndlessRecyclerViewScrollListener scrollListener =
                new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (timelineAdapter.getItemCount() >= page_size) {
                    queryFollowingPosts(page); // Loads the next set of posts
                }
            }
        };
        rvTrips.addOnScrollListener(scrollListener);

        // Adds swipe to refresh listener
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(true);
                queryFollowingPosts(0); // Queries from parse network
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void queryFollowingPosts(final int offset) {
        // Clears the adapter if querying top posts
        if (offset == 0) {
            timelineAdapter.clear();
        }
        // Queries for all the users that the current user follows
        ParseQuery<ParseObject> followingQuery = new ParseQuery<>("Follow");
        followingQuery.whereEqualTo("from", ParseUser.getCurrentUser());
        followingQuery.include("to");
        followingQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    // Creates a query of trips from each user the current user follows
                    List<ParseQuery<Trip>> queries = new ArrayList<>();
                    for (int i = 0; i < objects.size(); i++) {
                        ParseQuery<Trip> query = new ParseQuery<Trip>(Trip.class);
                        query.whereEqualTo("owner", objects.get(i).getParseUser("to"));
                        queries.add(query);
                    }

                    // Creates a query of trips from the current user
                    ParseQuery<Trip> queryCurrent = new ParseQuery<Trip>(Trip.class);
                    queryCurrent.whereEqualTo("owner", ParseUser.getCurrentUser());
                    queries.add(queryCurrent);

                    // Sends only one query to the Parse network
                    ParseQuery<Trip> compoundQuery = ParseQuery.or(queries);
                    compoundQuery.setSkip(offset * page_size);
                    compoundQuery.setLimit(page_size);
                    compoundQuery.include(Trip.KEY_OWNER);
                    compoundQuery.addDescendingOrder(Trip.KEY_CREATED_AT);
                    compoundQuery.findInBackground(new FindCallback<Trip>() {
                        @Override
                        public void done(List<Trip> trips, ParseException e) {
                            swipeContainer.setRefreshing(false);
                            if (e == null) {
                                progressDialog.hide();
                                if (trips.size() == 0) {
                                    showAlertDialog("No posts to load.");
                                }
                                timelineAdapter.addAll(trips);
                                // Scrolls to position left off at
                                if (getArguments() != null) {
                                    Bundle bundle = getArguments();
                                    int scrollPosition = bundle.getInt("indexToScrollTo");
                                    linearLayoutManager.scrollToPositionWithOffset(scrollPosition, PIXEL_OFFSET);
                                }

                            } else {
                                progressDialog.hide();
                                e.printStackTrace();
                                showAlertDialog("Error loading timeline.");
                            }
                        }
                    });
                } else {
                    progressDialog.hide();
                    e.printStackTrace();
                    showAlertDialog("Error loading timeline.");
                }
            }
        });
    }

    private void showAlertDialog(String message) {
        AlertDialog dialog = new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                .setTitle(message)
                .setPositiveButton("OK", null)
                .create();
        dialog.show();
    }
}
