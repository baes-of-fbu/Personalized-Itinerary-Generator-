package com.codepath.travelapp.Fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.travelapp.Activities.MainActivity;
import com.codepath.travelapp.Adapters.CurrentTripAdapter;
import com.codepath.travelapp.Adapters.PreviousTripAdapter;
import com.codepath.travelapp.Adapters.UpcomingTripAdapter;
import com.codepath.travelapp.Models.Trip;
import com.codepath.travelapp.Models.User;
import com.codepath.travelapp.OnSwipeTouchListener;
import com.codepath.travelapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL;
import static com.parse.ParseUser.getCurrentUser;

public class ProfileFragment extends Fragment {

    private final String TAG = "ProfileFragment";
    private UpcomingTripAdapter upcomingTripAdapter;
    private PreviousTripAdapter previousTripAdapter;
    private CurrentTripAdapter currentTripAdapter;
//    private Fragment sidebarFragment; TODO check if needed

    private int pageSize = 10;

    private User userProfile;
    private User userCurrent;
    private Map<String, ParseObject> following;
    private List<User> currentFollowing;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_profile,container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        following = new HashMap<>();
        currentFollowing = new ArrayList<>();

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
                    userProfile = (User) objects.get(0);
                    userCurrent = (User) getCurrentUser();

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
                    query.whereEqualTo("from", ParseUser.getCurrentUser());
                    query.include("to");
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> followList, ParseException e) {
                            if (e == null) {
                                for (int i = 0; i < followList.size(); i++) {
                                    currentFollowing.add((User) followList.get(i).get("to"));
                                    following.put(((User) followList.get(i).get("to")).getUsername(), followList.get(i));
                                }
                                FillInLayout(view);
                                SideSwipe(view);
                            }
                        }
                    });
                }
            });
        }
    }

    private void queryUpcomingPosts(ParseUser user) {
        upcomingTripAdapter.clear();

        ParseQuery<Trip> tripQuery = new ParseQuery<>(Trip.class);
        tripQuery.setLimit(pageSize);
        tripQuery.include(Trip.KEY_OWNER);
        tripQuery.whereGreaterThan(Trip.KEY_STARTDATE, Calendar.getInstance().getTime());
        tripQuery.whereEqualTo(Trip.KEY_OWNER, user);
        tripQuery.addAscendingOrder(Trip.KEY_STARTDATE);
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
        tripQuery.whereLessThan(Trip.KEY_ENDDATE, Calendar.getInstance().getTime());
        tripQuery.whereEqualTo(Trip.KEY_OWNER, user);
        tripQuery.addAscendingOrder(Trip.KEY_ENDDATE);
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

    private void queryCurrentPosts(ParseUser user) {
        currentTripAdapter.clear();

        ParseQuery<Trip> tripQuery = new ParseQuery<>(Trip.class);
        tripQuery.setLimit(pageSize);
        tripQuery.include(Trip.KEY_OWNER);
        tripQuery.whereLessThan(Trip.KEY_STARTDATE, Calendar.getInstance().getTime());
        tripQuery.whereGreaterThan(Trip.KEY_ENDDATE, Calendar.getInstance().getTime());
        tripQuery.whereEqualTo(Trip.KEY_OWNER, user);
        tripQuery.addAscendingOrder(Trip.KEY_STARTDATE);
        tripQuery.findInBackground(new FindCallback<Trip>() {
            @Override
            public void done(List<Trip> trips, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error");
                    e.printStackTrace();
                    return;
                }
                currentTripAdapter.addAll(trips);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @SuppressLint("SetTextI18n")
    private void FillInLayout(View view) {
        RecyclerView rvUpcoming = view.findViewById(R.id.rvUpcoming);
        RecyclerView rvPrevious = view.findViewById(R.id.rvPrevious);
        RecyclerView rvCurrent = view.findViewById(R.id.rvCurrent);
        TextView tvUsername = view.findViewById(R.id.tvUsername);
        TextView tvHometown = view.findViewById(R.id.tvHometown);
        TextView tvBio = view.findViewById(R.id.tvBio);
        ImageView ivProfileImage = view.findViewById(R.id.ivProfileImage);
        final Button btnFollowingStatus = view.findViewById(R.id.btnFollowingStatus);
        final TextView tvFollowersCount = view.findViewById(R.id.tvFollowersCount);
        final TextView tvFollowingCount = view.findViewById(R.id.tvFollowingCount);
        TextView tvFavoritesCount = view.findViewById(R.id.tvFavoriteCount);
        TextView tvFollowers = view.findViewById(R.id.tvFollowers);
        TextView tvFollowing = view.findViewById(R.id.tvFollowing);

        if (userProfile.getUsername().equals(userCurrent.getUsername())) {
            btnFollowingStatus.setVisibility(View.GONE);
        } else {
            btnFollowingStatus.setVisibility(View.VISIBLE);
            if (following.containsKey(userProfile.getUsername())) {
                btnFollowingStatus.setBackgroundColor(Color.GRAY);
                btnFollowingStatus.setText(getString(R.string.following));
            }
        }

        //Populate views in Profile Fragment
        tvUsername.setText(userProfile.getUsername());
        tvHometown.setText(userProfile.getHomeState());
        tvBio.setText(userProfile.getBio());
        if (userProfile.getProfileImage() != null && getContext() != null) {
            ParseFile image = userProfile.getProfileImage();
            Glide.with(getContext())
                    .load(image.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfileImage);
        }

        updateFollowCnt(tvFollowersCount, tvFollowingCount);

        tvFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO open dialogue to show list of followers and can click on a different user and go to their profile
            }
        });
        tvFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO open dialogue to show list of following and can click on a different user and go to their profile
            }
        });


        //create the upcomingTripAdapter
        ArrayList<Trip> upcomingTrips = new ArrayList<>();
        final ArrayList<Trip> previousTrips = new ArrayList<>();
        ArrayList<Trip> currentTrips = new ArrayList<>();
        //create the data source
        upcomingTripAdapter = new UpcomingTripAdapter(upcomingTrips);
        previousTripAdapter = new PreviousTripAdapter(previousTrips);
        currentTripAdapter = new CurrentTripAdapter(currentTrips);
        // initialize the linear layout manager
        LinearLayoutManager upcomingLayoutManager = new LinearLayoutManager(getContext(), HORIZONTAL, false);
        LinearLayoutManager previousLayoutManager = new LinearLayoutManager(getContext(),HORIZONTAL,false);
        LinearLayoutManager currentLayoutManager = new LinearLayoutManager(getContext(),HORIZONTAL, false);
        // set the layout manager on the recycler view
        rvUpcoming.setLayoutManager(upcomingLayoutManager);
        rvPrevious.setLayoutManager(previousLayoutManager);
        rvCurrent.setLayoutManager(currentLayoutManager);
        // set the adapters
        rvUpcoming.setAdapter(upcomingTripAdapter);
        rvPrevious.setAdapter(previousTripAdapter);
        rvCurrent.setAdapter(currentTripAdapter);
        // query posts for each view
        queryUpcomingPosts(userProfile);
        queryPreviousPosts(userProfile);
        queryCurrentPosts(userProfile);

        // Set onClick listener for follow/currentFollowing button
        btnFollowingStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "HELLO", Toast.LENGTH_SHORT).show();
                if (following.containsKey(userProfile.getUsername())) {
                    following.get(userProfile.getUsername()).deleteInBackground();
                    following.remove(userProfile.getUsername());

                    btnFollowingStatus.setBackgroundColor(getResources().getColor(R.color.LightSkyBlue));
                    btnFollowingStatus.setText(getString(R.string.follow));
                } else {
                    ParseObject follow = new ParseObject("Follow");
                    follow.put("from", userCurrent);
                    follow.put("to", userProfile);
                    follow.saveInBackground();

                    following.put(userProfile.getUsername(), follow);

                    btnFollowingStatus.setBackgroundColor(Color.LTGRAY);
                    btnFollowingStatus.setText(getString(R.string.following));
                }
                updateFollowCnt(tvFollowersCount, tvFollowingCount);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateFollowCnt(TextView tvFollowersCount, TextView tvFollowingCount) {
//        tvFollowersCount.setText(Integer.toString(profileFollowers.size()));
//        tvFollowingCount.setText(Integer.toString(profileFollowing.size()));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void SideSwipe (View view) {
        ConstraintLayout clProfile = view.findViewById(R.id.clProfile);
        clProfile.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            @Override
            public void onSwipeLeft() {
                if (getCurrentUser().getUsername().equals(userProfile.getUsername())) {
                    SidebarFragment fragment = new SidebarFragment();
                    Bundle userBundle = new Bundle();
                    userBundle.putParcelable("User", getCurrentUser());
                    fragment.setArguments(userBundle);
                    MainActivity.fragmentManager.beginTransaction()
                            .replace(R.id.flContainer, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
    }

    // Get a list of users a give userProfile is currentFollowing and then makes calls to fill in layout and set up side swipe
    private void getFollowing(ParseRelation<User> relation, final View view) {
        relation.getQuery().findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
//                        currentFollowing.add(objects.get(i).getObjectId());
                    }
                    FillInLayout(view);
                    SideSwipe(view);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getUserList(ParseRelation<User> relation, final List<User> users) {
        relation.getQuery().findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> objects, ParseException e) {
                if (e == null) {
                    users.addAll(objects);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
