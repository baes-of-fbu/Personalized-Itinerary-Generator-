package com.codepath.travelapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.travelapp.Adapters.FavoriteTripAdapter;
import com.codepath.travelapp.Adapters.PreviousTripAdapter;
import com.codepath.travelapp.Adapters.UpcomingTripAdapter;
import com.codepath.travelapp.Models.Trip;
import com.codepath.travelapp.Models.User;
import com.codepath.travelapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL;

public class ProfileFragment extends Fragment {

    private final String TAG = "ProfileFragment";
    private UpcomingTripAdapter upcomingTripAdapter;
    private PreviousTripAdapter previousTripAdapter;
    private FavoriteTripAdapter favoriteTripAdapter;

    private int pageSize = 10;
    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_profile,container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle userBundle = getArguments();
        if (userBundle != null) {
            String username = userBundle.getString("username");
            ParseQuery<ParseUser> userQuery = new ParseQuery<>(ParseUser.class);
            userQuery.whereEqualTo("username", username);
            userQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e != null) {
                        Log.e("ProfileFragment", "Error");
                        e.printStackTrace();
                        return;
                    }
                    Log.d("ComposeFragment", objects.toString());
                    user = (User) objects.get(0);
                    FillInLayout(view);
                }
            });
        }
    }

    private void queryUpcomingPosts(ParseUser user) {
        upcomingTripAdapter.clear();

        ParseQuery<Trip> tripQuery = new ParseQuery<>(Trip.class);

        tripQuery.setLimit(pageSize);
        tripQuery.include(Trip.KEY_OWNER);
        tripQuery.whereEqualTo(Trip.KEY_ISUPCOMING, true);
        tripQuery.whereEqualTo(Trip.KEY_OWNER, user);
        tripQuery.addDescendingOrder(Trip.KEY_CREATED_AT);
        tripQuery.findInBackground(new FindCallback<Trip>() {
            @Override
            public void done(List<Trip> trips, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error");
                    e.printStackTrace();
                    return;
                }
                upcomingTripAdapter.addAll(trips);
            }
        });
    }

    private void queryPreviousPosts(ParseUser user) {
        previousTripAdapter.clear();

        ParseQuery<Trip> tripQuery = new ParseQuery<>(Trip.class);

        tripQuery.setLimit(pageSize);
        tripQuery.include(Trip.KEY_OWNER);
        // TODO query by time
        tripQuery.whereEqualTo(Trip.KEY_ISUPCOMING, false);
        tripQuery.whereEqualTo(Trip.KEY_OWNER, user);
        tripQuery.addDescendingOrder(Trip.KEY_CREATED_AT);
        tripQuery.findInBackground(new FindCallback<Trip>() {
            @Override
            public void done(List<Trip> trips, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error");
                    e.printStackTrace();
                    return;
                }
                previousTripAdapter.addAll(trips);
            }
        });
    }

    private void queryFavoritePosts(ParseUser user) {
        favoriteTripAdapter.clear();

        ParseQuery<Trip> tripQuery = new ParseQuery<>(Trip.class);

        tripQuery.setLimit(pageSize);
        tripQuery.include(Trip.KEY_OWNER);
        // TODO query by time
        tripQuery.whereEqualTo(Trip.KEY_ISFAVORITED, true);
        tripQuery.whereEqualTo(Trip.KEY_OWNER, user);
        tripQuery.addDescendingOrder(Trip.KEY_CREATED_AT);
        tripQuery.findInBackground(new FindCallback<Trip>() {
            @Override
            public void done(List<Trip> trips, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error");
                    e.printStackTrace();
                    return;
                }
                favoriteTripAdapter.addAll(trips);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void FillInLayout(View view) {
        RecyclerView rvUpcoming = view.findViewById(R.id.rvUpcoming);
        RecyclerView rvPrevious = view.findViewById(R.id.rvPrevious);
        RecyclerView rvFavorite = view.findViewById(R.id.rvFavorite);
        TextView tvUsername = view.findViewById(R.id.tvUsername);
        TextView tvHometown = view.findViewById(R.id.tvHometown);
        TextView tvBio = view.findViewById(R.id.tvBio);
        ImageView ivProfileImage = view.findViewById(R.id.ivProfileImage);
        TextView tvFollowersCount = view.findViewById(R.id.tvFollowersCount);
        TextView tvFollowingCount = view.findViewById(R.id.tvFollowingCount);
        TextView tvFavoritesCOunt = view.findViewById(R.id.tvFavoriteCcount);


        //Populate views in Profile Fragment
        tvUsername.setText(user.getUsername());
        tvHometown.setText(user.getHomeState());
        tvBio.setText(user.getBio());
        if (user.getProfileImage() != null) {
            ParseFile image = user.getProfileImage();
            Glide.with(getContext())
                    .load(image.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfileImage);
        }
        tvFollowersCount.setText(user.getFollowers().toString());
        tvFollowingCount.setText(user.getFollowing().toString());
        tvFavoritesCOunt.setText(user.getFavorites().toString());


        //create the upcomingTripAdapter
        ArrayList<Trip> upcomingTrips = new ArrayList<>();
        ArrayList<Trip> previousTrips = new ArrayList<>();
        ArrayList<Trip> favoriteTrips = new ArrayList<>();
        //create the data source
        upcomingTripAdapter = new UpcomingTripAdapter(upcomingTrips);
        previousTripAdapter = new PreviousTripAdapter(previousTrips);
        favoriteTripAdapter = new FavoriteTripAdapter(favoriteTrips);
        // set the layout manager on the recycler view
        LinearLayoutManager upcomingLayoutManager = new LinearLayoutManager(getContext(), HORIZONTAL, false);
        LinearLayoutManager previousLayoutManager = new LinearLayoutManager(getContext(),HORIZONTAL,false);
        LinearLayoutManager favoriteLayoutManager = new LinearLayoutManager(getContext(),HORIZONTAL, false);

        rvUpcoming.setLayoutManager(upcomingLayoutManager);
        rvPrevious.setLayoutManager(previousLayoutManager);
        rvFavorite.setLayoutManager(favoriteLayoutManager);

        rvUpcoming.setAdapter(upcomingTripAdapter);
        rvPrevious.setAdapter(previousTripAdapter);
        rvFavorite.setAdapter(favoriteTripAdapter);

        queryUpcomingPosts(user);
        queryPreviousPosts(user);
        queryFavoritePosts(user);

        Toast.makeText(getContext(), "Welcome to Your Profile",Toast.LENGTH_SHORT).show();
    }
}
