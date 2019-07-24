package com.codepath.travelapp.fragments;

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
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.travelapp.Adapters.FavoriteTripAdapter;
import com.codepath.travelapp.Adapters.PreviousTripAdapter;
import com.codepath.travelapp.Adapters.UpcomingTripAdapter;
import com.codepath.travelapp.Models.Trip;
import com.codepath.travelapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL;
import static com.parse.ParseUser.getCurrentUser;

public class ProfileFragment extends Fragment {

    private final String TAG = "ProfileFragment";
    private RecyclerView rvUpcoming;
    private  RecyclerView rvPrevious;
    private RecyclerView rvFavorite;
    protected UpcomingTripAdapter adapter;
    protected PreviousTripAdapter adapter2;
    protected FavoriteTripAdapter adapter3;

    protected ArrayList<Trip> mTrips;
    protected ArrayList<Trip> nTrips;
    protected ArrayList<Trip> oTrips;
    private int pagesize = 10;
    private TextView tvUsername;
    private TextView tvHometown;
    private ImageView ivProfileImage;
    private SwipeRefreshLayout swipeContainer;
    private TextView tvFollowingCount;
    private TextView tvFollowersCount;
    private TextView tvFavoritesCOunt;
    private TextView tvBio;
    private Toolbar toolbar;
    private String username;
    private ParseUser user;


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
            username = userBundle.getString("username");
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
                    user = (ParseUser) objects.get(0);
                    FillInLayout(view);
                }
            });
        }



    }
    protected void queryUpcomingPosts(ParseUser user) {

        adapter.clear();

        ParseQuery<Trip> tripQuery = new ParseQuery<Trip>(Trip.class);

        tripQuery.setLimit(pagesize);
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
                adapter.addAll(trips);
            }
        });
    }
    protected void queryPreviousPosts(ParseUser user) {

        adapter2.clear();

        ParseQuery<Trip> tripQuery = new ParseQuery<Trip>(Trip.class);

        tripQuery.setLimit(pagesize);
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
                adapter2.addAll(trips);
            }
        });
    }
    protected void queryFavoritePosts(ParseUser user) {

        adapter3.clear();

        ParseQuery<Trip> tripQuery = new ParseQuery<Trip>(Trip.class);

        tripQuery.setLimit(pagesize);
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
                adapter3.addAll(trips);
            }
        });
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    public void FillInLayout(View view) {
        rvUpcoming = view.findViewById(R.id.rvUpcoming);
        rvPrevious = view.findViewById(R.id.rvPrevious);
        rvFavorite = view.findViewById(R.id.rvFavorite);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvHometown = view.findViewById(R.id.tvHometown);
        tvBio = view.findViewById(R.id.tvBio);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvFollowersCount = view.findViewById(R.id.tvFollowersCount);
        tvFollowingCount = view.findViewById(R.id.tvFollowingCount);
        tvFavoritesCOunt = view.findViewById(R.id.tvFavoriteCcount);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbarMain);


        //Populate views in Profile Fragment
        tvUsername.setText(user.getUsername());
        tvHometown.setText((String) user.get("homeState"));
        tvBio.setText((String)user.get("bio"));
        if (user.get("profileImage") != null) {
            ParseFile image = (ParseFile) user.get("profileImage");
            Glide.with(getContext())
                    .load(image.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfileImage);
        }
//        tvFollowersCount.setText(user.get("followers").toString());
//        tvFollowingCount.setText(user.get("following").toString());
//        tvFavoritesCOunt.setText(user.get("favorites").toString());

        //create the adapter
        mTrips = new ArrayList<>();
        nTrips = new ArrayList<>();
        oTrips = new ArrayList<>();
        //create the data source
        adapter = new UpcomingTripAdapter(mTrips);
        adapter2 = new PreviousTripAdapter(nTrips);
        adapter3 = new FavoriteTripAdapter(oTrips);
        // set the layout manager on the recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), HORIZONTAL, false);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext(),HORIZONTAL,false);
        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(getContext(),HORIZONTAL, false);

        rvUpcoming.setLayoutManager(linearLayoutManager);
        rvPrevious.setLayoutManager(linearLayoutManager2);
        rvFavorite.setLayoutManager(linearLayoutManager3);

        rvUpcoming.setAdapter(adapter);
        rvPrevious.setAdapter(adapter2);
        rvFavorite.setAdapter(adapter3);

        queryUpcomingPosts(user);
        queryPreviousPosts(user);
        queryFavoritePosts(user);

        Toast.makeText(getContext(), "Welcome to Your Profile",Toast.LENGTH_SHORT).show();
    }
}
