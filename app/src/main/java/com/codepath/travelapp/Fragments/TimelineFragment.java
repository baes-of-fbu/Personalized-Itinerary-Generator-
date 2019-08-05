package com.codepath.travelapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class TimelineFragment extends Fragment {

    private final String TAG = "TimelineFragment";

    protected TimelineAdapter adapter;
    private SwipeRefreshLayout swipeContainer;

    private Integer skip = 0;
    private boolean loadMore = false;
    private final int page_size = 10;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView rvTrips = view.findViewById(R.id.rvTrips);

        // create the upcomingTripAdapter and set the layout manager on the recycler view
        ArrayList<Trip> mTrips = new ArrayList<>();
        adapter = new TimelineAdapter(mTrips);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvTrips.setLayoutManager(linearLayoutManager);
        rvTrips.setAdapter(adapter);

        // Set up the scroll listener and add it to RecyclerView
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
               loadNextDataFromApi(page);
            }
        };
        rvTrips.addOnScrollListener(scrollListener);

        queryFollowingPosts();

        // Swipe Container/ refresh code
        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(true);
                queryFollowingPosts();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void queryFollowingPosts() { //TODO test this
        adapter.clear();
        ParseQuery<ParseObject> followingQuery = new ParseQuery<>("Follow");
        followingQuery.whereEqualTo("from", ParseUser.getCurrentUser());
        followingQuery.include("toId");
        followingQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    List<ParseQuery<Trip>> queries = new ArrayList<>();
                    for (int i = 0; i < objects.size(); i++) {
                        ParseQuery<Trip> query = new ParseQuery<Trip>(Trip.class);
                        query.whereEqualTo("owner", objects.get(i).getString("toId"));
                        queries.add(query);
                    }

                    ParseQuery<Trip> queryCurrent = new ParseQuery<Trip>(Trip.class);
                    queryCurrent.whereEqualTo("owner", ParseUser.getCurrentUser());
                    queries.add(queryCurrent);

                    ParseQuery<Trip> compoundQuery = ParseQuery.or(queries);
                    compoundQuery.setLimit(page_size);
                    compoundQuery.include(Trip.KEY_OWNER);
                    compoundQuery.addDescendingOrder(Trip.KEY_CREATED_AT);
                    compoundQuery.findInBackground(new FindCallback<Trip>() {
                        @Override
                        public void done(List<Trip> trips, ParseException e) {
                            swipeContainer.setRefreshing(false);
                            if (e == null) {
                                adapter.addAll(trips);
                            } else {
                                Log.e(TAG,"Error");
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    // Query all trips and add them to the adapter to populate the Timeline TODO remove this 
    private void queryPosts() {
        adapter.clear();
        ParseQuery<Trip> tripQuery = new ParseQuery<>(Trip.class);
        tripQuery.setLimit(page_size);
        tripQuery.include(Trip.KEY_OWNER);
        tripQuery.addDescendingOrder(Trip.KEY_CREATED_AT);
        tripQuery.findInBackground(new FindCallback<Trip>() {
            @Override
            public void done(List<Trip> trips, ParseException e) {
                swipeContainer.setRefreshing(false);
                if (e == null) {
                    adapter.addAll(trips);
                } else {
                    Log.e(TAG,"Error");
                    e.printStackTrace();
                }
            }
        });
    }

    // Append the next page of data into the adapter
    private void loadNextDataFromApi(int offset) {
        ParseQuery<Trip> tripQuery = new ParseQuery<>(Trip.class);
        tripQuery.include(Trip.KEY_OWNER);

        // True is there are more posts to load
        if(loadMore) {
            tripQuery.setSkip(offset*page_size);
        }

        tripQuery.setLimit(page_size);
        tripQuery.addDescendingOrder(Trip.KEY_CREATED_AT);
        tripQuery.findInBackground(new FindCallback<Trip>() {
            @Override
            public void done(List<Trip> objects, ParseException e) {
                if (e == null) {
                    Log.d("DEBUG", "Adding more posts to timeline");
                    skip = skip + objects.size();
                    if(objects.size() == 0) {
                        loadMore = false;
                    } else {
                        loadMore = true;
                        adapter.clear();
                        adapter.addAll(objects);
                    }
                } else {
                    e.printStackTrace();
                    Log.d("DEBUG", "Fetch timeline error: " + e.toString());
                }
            }
        });
    }
}
