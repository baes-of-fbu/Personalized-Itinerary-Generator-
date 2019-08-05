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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.travelapp.Activities.MainActivity;
import com.codepath.travelapp.Adapters.AchievementAdapter;
import com.codepath.travelapp.Adapters.CurrentTripAdapter;
import com.codepath.travelapp.Adapters.PreviousTripAdapter;
import com.codepath.travelapp.Adapters.UpcomingTripAdapter;
import com.codepath.travelapp.Models.Achievement;
import com.codepath.travelapp.Models.Trip;
import com.codepath.travelapp.Models.User;
import com.codepath.travelapp.OnSwipeTouchListener;
import com.codepath.travelapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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
    private AchievementAdapter achievementAdapter;
    private PreviousTripAdapter savedTripAdapter;

    private int pageSize = 10;

    private User userProfile;
    private Map<String, ParseObject> following;
    private Map<User, ParseObject> followers;

    private User userCurrent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        following = new HashMap<>();
        followers = new HashMap<>();

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

                    List<ParseQuery<ParseObject>> queries = new ArrayList<>();

                    ParseQuery<ParseObject> queryFrom = ParseQuery.getQuery("Follow");
                    queryFrom.whereEqualTo("from", userProfile);
                    queries.add(queryFrom);

                    ParseQuery<ParseObject> queryTo = ParseQuery.getQuery("Follow");
                    queryTo.whereEqualTo("toId", userProfile.getObjectId());
                    queries.add(queryTo);

                    ParseQuery<ParseObject> compoundQuery = ParseQuery.or(queries);
                    compoundQuery.include("from");
                    compoundQuery.include("toId");
                    compoundQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> followList, ParseException e) {
                            if (e == null) {
                                for (int i = 0; i < followList.size(); i++) {
                                    if (followList.get(i).getString("toId").equals(userProfile.getObjectId())) {
                                        if (((User) followList.get(i).get("from")).getObjectId().equals(userCurrent.getObjectId())) {
                                            followers.put(userCurrent, followList.get(i));
                                        } else {
                                            followers.put(((User) followList.get(i).get("from")), followList.get(i));
                                        }
                                    } else if (((User) followList.get(i).get("from")).getObjectId().equals(userProfile.getObjectId())) {
                                        following.put(followList.get(i).getString("toId"), followList.get(i));
                                    }
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
                    Log.e(TAG, "Error");
                    e.printStackTrace();
                    return;
                }
                upcomingTripAdapter.addAll(trips);
            }
        });
    }

    private void queryPreviousPosts(final User user) {
        previousTripAdapter.clear();

        ParseQuery<Trip> tripQuery = new ParseQuery<>(Trip.class);
        tripQuery.setLimit(pageSize);
        tripQuery.include(Trip.KEY_OWNER);
        tripQuery.whereLessThan(Trip.KEY_ENDDATE, Calendar.getInstance().getTime());
        tripQuery.whereEqualTo(Trip.KEY_OWNER, user);
        tripQuery.addDescendingOrder(Trip.KEY_ENDDATE);
        tripQuery.findInBackground(new FindCallback<Trip>() {
            @Override
            public void done(List<Trip> trips, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error");
                    e.printStackTrace();
                    return;
                }
                previousTripAdapter.addAll(trips);

                if(user.getUsername().equals(userCurrent.getUsername())){
                    queryNewAchievements(trips, user);
                }
            }
        });
    }

    private void queryNewAchievements(List<Trip> trips, final User user) {
        ParseQuery<Achievement> achievementQuery = new ParseQuery<>(Achievement.class);
        if (trips.size() > 0) {
            if (trips.size() > 5) {
                achievementQuery.whereEqualTo("name", "Adventurer");
            }
            if (trips.size() >= 1) {
                achievementQuery.whereEqualTo("name", "Backpacker");
            }

            achievementQuery.findInBackground(new FindCallback<Achievement>() {
                @Override
                public void done(final List<Achievement> objects, ParseException e) {
                    if (e == null) {
                        for (int i = 0; i < objects.size(); i++) {
                            user.getAchievementRelation().add(objects.get(i));
                        }
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    achievementAdapter.addAll(objects);
                                }
                            }
                        });
                    }
                }
            });
        }
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
                    Log.e(TAG, "Error");
                    e.printStackTrace();
                    return;
                }
                currentTripAdapter.addAll(trips);
            }
        });
    }

    private void querySaved(final ParseUser user) {
        savedTripAdapter.clear();
        final List<Trip> mTrips = new ArrayList<>();

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("SavedTrip");
        query.include("user");
        query.include("trip");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> savedList, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < savedList.size(); i++) {
                        if (((User) savedList.get(i).get("user")).getObjectId().equals(user.getObjectId())) {
                            mTrips.add((Trip) savedList.get(i).get("trip"));
                        }
                    }
                    savedTripAdapter.addAll(mTrips);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void queryAchievements() {
        ParseQuery<Achievement> achievementParseQuery = userProfile.getAchievementRelation().getQuery();
        achievementParseQuery.findInBackground(new FindCallback<Achievement>() {
            @Override
            public void done(List<Achievement> achievements, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "The query was successful");
                    achievementAdapter.addAll(achievements);
                } else {
                    Log.e(TAG, "Error");
                    e.printStackTrace();
                }
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
        RecyclerView rvSaved = view.findViewById(R.id.rvSaved);
        RecyclerView rvAchievements = view.findViewById(R.id.rvAchievements);
        TextView tvUsername = view.findViewById(R.id.tvUsername);
        TextView tvFullName = view.findViewById(R.id.tvFullName);
        TextView tvHometown = view.findViewById(R.id.tvHometown);
        TextView tvBio = view.findViewById(R.id.tvBio);
        ImageView ivProfileImage = view.findViewById(R.id.ivProfileImage);
        final Button btnFollowingStatus = view.findViewById(R.id.btnFollowingStatus);
        final TextView tvFollowersCount = view.findViewById(R.id.tvFollowersCount);
        final TextView tvFollowingCount = view.findViewById(R.id.tvFollowingCount);
        TextView tvFollowers = view.findViewById(R.id.tvFollowers);
        TextView tvFollowing = view.findViewById(R.id.tvFollowing);

        if (userProfile.getObjectId().equals(userCurrent.getObjectId())) {
            MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
            btnFollowingStatus.setVisibility(View.GONE);
        } else {
            MainActivity.bottomNavigationView.setVisibility(View.GONE);
            btnFollowingStatus.setVisibility(View.VISIBLE);
            if (followers.containsKey(userCurrent)) {
                btnFollowingStatus.setBackgroundColor(Color.GRAY);
                btnFollowingStatus.setText(getString(R.string.following));
            }
        }

        //Populate views in Profile Fragment
        tvUsername.setText(userProfile.getUsername());
        tvFullName.setText(userProfile.getFullName());
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
                FragmentManager fragmentManager = MainActivity.fragmentManager;
                ListDialogFragment listDialogFragment = new ListDialogFragment().newInstance(getFromList(), "Followers");
                listDialogFragment.show(fragmentManager, "fragment_list_dialog");
            }
        });
        tvFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                FragmentManager fragmentManager = MainActivity.fragmentManager;
//                ListDialogFragment listDialogFragment = new ListDialogFragment().newInstance(getToList(), "Following");
//                listDialogFragment.show(fragmentManager, "fragment_list_dialog");
            }
        });


        //create the upcomingTripAdapter
        ArrayList<Trip> upcomingTrips = new ArrayList<>();
        ArrayList<Trip> previousTrips = new ArrayList<>();
        ArrayList<Trip> currentTrips = new ArrayList<>();
        ArrayList<Trip> savedTrips = new ArrayList<>();
        ArrayList<Achievement> achievements = new ArrayList<>();
        //create the data source
        upcomingTripAdapter = new UpcomingTripAdapter(upcomingTrips);
        previousTripAdapter = new PreviousTripAdapter(previousTrips);
        currentTripAdapter = new CurrentTripAdapter(currentTrips);
        savedTripAdapter = new PreviousTripAdapter(savedTrips);
        achievementAdapter = new AchievementAdapter(achievements);
        // initialize the linear layout manager
        LinearLayoutManager upcomingLayoutManager = new LinearLayoutManager(getContext(), HORIZONTAL, false);
        LinearLayoutManager previousLayoutManager = new LinearLayoutManager(getContext(), HORIZONTAL, false);
        LinearLayoutManager currentLayoutManager = new LinearLayoutManager(getContext(), HORIZONTAL, false);
        LinearLayoutManager savedLayoutManager = new LinearLayoutManager(getContext(), HORIZONTAL, false);
        LinearLayoutManager achievementLayoutManager = new LinearLayoutManager(getContext(), HORIZONTAL, false);
        // set the layout manager on the recycler view
        rvUpcoming.setLayoutManager(upcomingLayoutManager);
        rvPrevious.setLayoutManager(previousLayoutManager);
        rvCurrent.setLayoutManager(currentLayoutManager);
        rvSaved.setLayoutManager(savedLayoutManager);
        rvAchievements.setLayoutManager(achievementLayoutManager);
        // set the adapters
        rvUpcoming.setAdapter(upcomingTripAdapter);
        rvPrevious.setAdapter(previousTripAdapter);
        rvCurrent.setAdapter(currentTripAdapter);
        rvSaved.setAdapter(savedTripAdapter);
        rvAchievements.setAdapter(achievementAdapter);
        // query posts for each view
        queryUpcomingPosts(userProfile);
        queryPreviousPosts(userProfile);
        queryCurrentPosts(userProfile);
        if(!userProfile.getUsername().equals(userCurrent.getUsername())) {
            queryAchievements();
        }
        querySaved(userProfile);


        // Set onClick listener for follow/currentFollowing button
        btnFollowingStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (followers.containsKey(userCurrent)) {
                    followers.get(userCurrent).deleteInBackground();
                    followers.remove(userCurrent);

                    btnFollowingStatus.setBackgroundColor(getResources().getColor(R.color.LightSkyBlue));
                    btnFollowingStatus.setText(getString(R.string.follow));
                    updateFollowCnt(tvFollowersCount, tvFollowingCount);
                } else {
                    final ParseObject follow = new ParseObject("Follow");
                    follow.put("from", userCurrent);
                    follow.put("toId", userProfile.getObjectId());
                    follow.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                followers.put(userCurrent, follow);

                                btnFollowingStatus.setBackgroundColor(Color.LTGRAY);
                                btnFollowingStatus.setText(getString(R.string.following));
                                updateFollowCnt(tvFollowersCount, tvFollowingCount);
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateFollowCnt(final TextView tvFollowersCount, final TextView tvFollowingCount) {
        tvFollowersCount.setText(Integer.toString(getFromList().size()));
        tvFollowingCount.setText(Integer.toString(getToList().size()));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void SideSwipe(View view) {
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

    private ArrayList<User> getFromList() {
        return new ArrayList<>(followers.keySet());
    }

    private ArrayList<String> getToList() {
        return new ArrayList<>(following.keySet());
    }
}