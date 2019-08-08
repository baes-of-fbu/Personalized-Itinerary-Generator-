package com.codepath.travelapp.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.travelapp.Activities.MainActivity;
import com.codepath.travelapp.Adapters.ProfileTripsAdapter;
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
import java.util.Objects;

import static androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL;
import static com.parse.ParseUser.getCurrentUser;

public class ProfileFragment extends Fragment {

    private ProfileTripsAdapter upcomingTripAdapter;
    private ProfileTripsAdapter previousTripAdapter;
    private ProfileTripsAdapter currentTripAdapter;
    private ProfileTripsAdapter savedTripAdapter;

    private RecyclerView rvUpcoming;
    private RecyclerView rvCurrent;
    private RecyclerView rvPrevious;
    private RecyclerView rvSaved;
    private Button btnFollowingStatus;
    private TextView tvFollowersCount;
    private TextView tvFollowingCount;
    private TextView tvFollowers;
    private TextView tvFollowing;
    private ImageButton btnSidebar;

    private int pageSize = 10;

    private User userProfile;
    private Map<User, ParseObject> following;
    private Map<User, ParseObject> followers;
    private Toolbar tbProfile;
    private User userCurrent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tbProfile = view.findViewById(R.id.tbProfile);
        following = new HashMap<>();
        followers = new HashMap<>();

        Bundle userBundle = getArguments();
        if (userBundle != null) {
            String username = userBundle.getString("username");

            if (username.equals(ParseUser.getCurrentUser().getUsername())) {
                tbProfile.setVisibility(View.GONE);
            } else {
                tbProfile.setNavigationIcon(R.drawable.ic_arrow_back);
                tbProfile.setTitle("Profile");
                tbProfile.setTitleTextColor(getContext().getResources().getColor(R.color.white));
                tbProfile.setBackgroundColor(getContext().getResources().getColor(R.color.LightSkyBlue));
                tbProfile.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Objects.requireNonNull(getActivity()).onBackPressed();
                    }
                });
            }

            ParseQuery<ParseUser> userQuery = new ParseQuery<>(ParseUser.class);
            userQuery.whereEqualTo("username", username);
            userQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {
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
                        compoundQuery.include("to");
                        compoundQuery.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> followList, ParseException e) {
                                if (e == null) {
                                    for (int i = 0; i < followList.size(); i++) {
                                        if (Objects.equals(followList.get(i).getString("toId"),
                                                userProfile.getObjectId())) {
                                            if (((User) Objects.requireNonNull(followList.get(i)
                                                    .get("from"))).getObjectId().equals(userCurrent
                                                    .getObjectId())) {
                                                followers.put(userCurrent, followList.get(i));
                                            } else {
                                                followers.put(((User) followList.get(i)
                                                        .get("from")), followList.get(i));
                                            }
                                        } else if (((User) Objects.requireNonNull(followList.get(i)
                                                .get("from"))).getObjectId()
                                                .equals(userProfile.getObjectId())) {
                                            following.put((User) followList.get(i).get("to"),
                                                    followList.get(i));
                                        }
                                    }
                                    FillInLayout(view);
                                    SideSwipe(view);
                                } else {
                                    e.printStackTrace();
                                    showAlertDialog("Error loading profile.");
                                }
                            }
                        });
                    } else {
                        e.printStackTrace();
                        showAlertDialog("Error loading profile.");
                    }
                }
            });
        }
    }

    private void queryNewAchievements(List<Trip> trips, final User user) {
        ParseQuery<Achievement> achievementQuery = new ParseQuery<>(Achievement.class);
        if (trips.size() > 0) {
            if (trips.size() > 25){
                achievementQuery.whereEqualTo("name", "Nomad");
            }
            if (trips.size() > 10) {
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
                        user.saveInBackground();
                    }
                }
            });
        }
    }

    private void queryUpcomingPosts(ParseUser user, final View view) {
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
                if (e == null) {
                    upcomingTripAdapter.addAll(trips);
                    TextView tvNoUpcoming = view.findViewById(R.id.tvNoUpcoming);

                    if (upcomingTripAdapter.getItemCount() == 0) {
                        tvNoUpcoming.setVisibility(View.VISIBLE);
                    }else {
                        tvNoUpcoming.setVisibility(View.GONE);
                    }
                } else {
                    e.printStackTrace();
                    showAlertDialog("Error loading profile.");
                }
            }
        });
    }

    private void queryPreviousPosts(final User user, final View view) {
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
                if (e == null) {
                    previousTripAdapter.addAll(trips);
                    queryNewAchievements(trips, userProfile);
                    TextView tvNoPrevious = view.findViewById(R.id.tvNoPrevious);

                    if (previousTripAdapter.getItemCount() == 0) {
                        tvNoPrevious.setVisibility(View.VISIBLE);
                    }else {
                        tvNoPrevious.setVisibility(View.GONE);
                    }
                } else {
                    e.printStackTrace();
                    showAlertDialog("Error loading profile.");
                }
            }
        });
    }

    private void queryCurrentPosts(ParseUser user, final View view) {
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
                if (e == null) {
                    currentTripAdapter.addAll(trips);
                    TextView tvNoCurrent = view.findViewById(R.id.tvNoCurrent);

                    if (currentTripAdapter.getItemCount() == 0) {
                        tvNoCurrent.setVisibility(View.VISIBLE);
                    }else {
                        tvNoCurrent.setVisibility(View.GONE);
                    }
                } else {
                    e.printStackTrace();
                    showAlertDialog("Error loading profile.");
                }
            }
        });
    }

    private void querySaved(final ParseUser user, final View view) {
        savedTripAdapter.clear();
        final List<Trip> mTrips = new ArrayList<>();

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("SavedTrip");
        query.include("user");
        query.include("trip");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> savedList, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < savedList.size(); i++) {
                        if (((User) Objects.requireNonNull(savedList.get(i).get("user")))
                                .getObjectId().equals(user.getObjectId())) {
                            mTrips.add((Trip) savedList.get(i).get("trip"));
                        }
                    }
                    savedTripAdapter.addAll(mTrips);
                    TextView tvNoSaved = view.findViewById(R.id.tvNoSaved);

                    if (savedTripAdapter.getItemCount() == 0) {
                        tvNoSaved.setVisibility(View.VISIBLE);
                    }else {
                        tvNoSaved.setVisibility(View.GONE);
                    }
                } else {
                    e.printStackTrace();
                    showAlertDialog("Error loading profile.");
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
        rvUpcoming = view.findViewById(R.id.rvUpcoming);
        rvPrevious = view.findViewById(R.id.rvPrevious);
        rvCurrent = view.findViewById(R.id.rvCurrent);
        rvSaved = view.findViewById(R.id.rvSaved);
        btnFollowingStatus = view.findViewById(R.id.btnFollowingStatus);
        tvFollowersCount = view.findViewById(R.id.tvFollowersCount);
        tvFollowingCount = view.findViewById(R.id.tvFollowingCount);
        tvFollowers = view.findViewById(R.id.tvFollowers);
        tvFollowing = view.findViewById(R.id.tvFollowing);
        btnSidebar = view.findViewById(R.id.btnSidebar);
        TextView tvUsername = view.findViewById(R.id.tvUsername);
        TextView tvFullName = view.findViewById(R.id.tvFullName);
        TextView tvHometown = view.findViewById(R.id.tvHometown);
        TextView tvBio = view.findViewById(R.id.tvBio);
        ImageView ivProfileImage = view.findViewById(R.id.ivProfileImage);



        // Only shows the bottom navigation bar if the current user is on his own profile page
        if (userProfile.getObjectId().equals(userCurrent.getObjectId())) {
            MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
            btnFollowingStatus.setVisibility(View.GONE);
            btnSidebar.setVisibility(View.VISIBLE);
        } else {
            MainActivity.bottomNavigationView.setVisibility(View.GONE);
            btnFollowingStatus.setVisibility(View.VISIBLE);
            btnSidebar.setVisibility(View.GONE);
            if (followers.containsKey(userCurrent)) {
                btnFollowingStatus.setBackgroundColor(getResources().getColor(R.color.white));
                btnFollowingStatus.setText(getString(R.string.following));
                btnFollowingStatus.setTextColor(getResources().getColor(R.color.LightSkyBlue));
            } else {
                btnFollowingStatus.setBackgroundColor(getResources().getColor(R.color.LightSkyBlue));
                btnFollowingStatus.setText(getString(R.string.follow));
                btnFollowingStatus.setTextColor(getResources().getColor(R.color.white));
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

        updateFollowCnt();
        populateTripRecyclerViews(view);

        addOnClickListeners();
    }

    private void populateTripRecyclerViews(View view) {
        // Create the upcomingTripAdapter
        ArrayList<Trip> upcomingTrips = new ArrayList<>();
        ArrayList<Trip> previousTrips = new ArrayList<>();
        ArrayList<Trip> currentTrips = new ArrayList<>();
        ArrayList<Trip> savedTrips = new ArrayList<>();

        // Create the data source
        upcomingTripAdapter = new ProfileTripsAdapter(upcomingTrips);
        previousTripAdapter = new ProfileTripsAdapter(previousTrips);
        currentTripAdapter = new ProfileTripsAdapter(currentTrips);
        savedTripAdapter = new ProfileTripsAdapter(savedTrips);

        // Initialize the linear layout manager
        LinearLayoutManager upcomingLayoutManager = new LinearLayoutManager(getContext(),
                HORIZONTAL, false);
        LinearLayoutManager previousLayoutManager = new LinearLayoutManager(getContext(),
                HORIZONTAL, false);
        LinearLayoutManager currentLayoutManager = new LinearLayoutManager(getContext(),
                HORIZONTAL, false);
        LinearLayoutManager savedLayoutManager = new LinearLayoutManager(getContext(),
                HORIZONTAL, false);

        // Set the layout manager on the recycler view
        rvUpcoming.setLayoutManager(upcomingLayoutManager);
        rvPrevious.setLayoutManager(previousLayoutManager);
        rvCurrent.setLayoutManager(currentLayoutManager);
        rvSaved.setLayoutManager(savedLayoutManager);

        // Set the adapters
        rvUpcoming.setAdapter(upcomingTripAdapter);
        rvPrevious.setAdapter(previousTripAdapter);
        rvCurrent.setAdapter(currentTripAdapter);
        rvSaved.setAdapter(savedTripAdapter);



        // Query posts for each view
        queryUpcomingPosts(userProfile, view);
        queryPreviousPosts(userProfile, view);
        queryCurrentPosts(userProfile, view);
        querySaved(userProfile, view);
    }

    private void addOnClickListeners() {

        // User can follow or un-follow another user
        btnFollowingStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (followers.containsKey(userCurrent)) {
                    followers.get(userCurrent).deleteInBackground();
                    followers.remove(userCurrent);

                    btnFollowingStatus.setBackgroundColor(getResources().getColor(R.color.LightSkyBlue));
                    btnFollowingStatus.setText(getString(R.string.follow));
                    btnFollowingStatus.setTextColor(getResources().getColor(R.color.white));
                } else {
                    final ParseObject follow = new ParseObject("Follow");
                    follow.put("from", userCurrent);
                    follow.put("toId", userProfile.getObjectId());
                    follow.put("to", userProfile);
                    follow.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                followers.put(userCurrent, follow);

                                btnFollowingStatus.setBackgroundColor(getResources().getColor(R.color.white));
                                btnFollowingStatus.setText(getString(R.string.following));
                                btnFollowingStatus.setTextColor(getResources().getColor(R.color.LightSkyBlue));
                            } else {
                                e.printStackTrace();
                                showAlertDialog("Unable to follow user");
                            }
                        }
                    });
                }
                updateFollowCnt();
            }
        });

        // User can see a list of the profile user's followers
        View.OnClickListener followersListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = MainActivity.fragmentManager;
                ListDialogFragment listDialogFragment = ListDialogFragment.newInstance(getFromList(),
                        "Followers");
                listDialogFragment.show(fragmentManager, "fragment_list_dialog");
            }
        };
        tvFollowers.setOnClickListener(followersListener);
        tvFollowersCount.setOnClickListener(followersListener);

        // User can see a list of who the profile user is following
        View.OnClickListener followingListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = MainActivity.fragmentManager;
                ListDialogFragment listDialogFragment = ListDialogFragment.newInstance(getToList(),
                        "Following");
                listDialogFragment.show(fragmentManager, "fragment_list_dialog");
            }
        };
        tvFollowing.setOnClickListener(followingListener);
        tvFollowingCount.setOnClickListener(followingListener);
        btnSidebar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SidebarFragment fragment = new SidebarFragment();
                Bundle userBundle = new Bundle();
                userBundle.putParcelable("User", getCurrentUser());
                fragment.setArguments(userBundle);
                MainActivity.fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.left_in, R.anim.left_out)
                        .replace(R.id.flContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateFollowCnt() {
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
                            .setCustomAnimations(R.anim.left_in, R.anim.left_out)
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

    private ArrayList<User> getToList() {
        return new ArrayList<>(following.keySet());
    }

    private void showAlertDialog(String message) {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(message)
                .setPositiveButton("OK", null)
                .create();
        dialog.show();
    }
}