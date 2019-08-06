package com.codepath.travelapp.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.travelapp.Activities.MainActivity;
import com.codepath.travelapp.Adapters.DayPlanAdapter;
import com.codepath.travelapp.Models.DayPlan;
import com.codepath.travelapp.Models.Trip;
import com.codepath.travelapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.relex.circleindicator.CircleIndicator2;

import static androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL;

public class TripDetailsFragment extends Fragment {

    private DayPlanAdapter adapter;
    private ArrayList<DayPlan> mDayPlan;

    private Trip trip;
    private ImageView ivPin;
    private ImageView ivShare;
    private TextView tvUsername;
    private TextView tvCityState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.bottomNavigationView.setVisibility(View.GONE);
        return inflater.inflate(R.layout.fragment_trip_details, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        final ImageView ivCoverPhoto = view.findViewById(R.id.ivCoverPhoto);
        ImageView ivProfileImage = view.findViewById(R.id.ivProfileImage);
        TextView tvTripName = view.findViewById(R.id.tvTripName);
        TextView tvTravelDates = view.findViewById(R.id.tvTravelDates);
        TextView tvDays = view.findViewById(R.id.tvDays);
        TextView tvBudget = view.findViewById(R.id.tvBudget);
        TextView tvCost = view.findViewById(R.id.tvCost);
        final RecyclerView rvSchedule = view.findViewById(R.id.rvSchedule);
        tvCityState = view.findViewById(R.id.tvCityState);
        tvUsername = view.findViewById(R.id.tvFullName);
        ivPin = view.findViewById(R.id.ivPin);
        ivShare = view.findViewById(R.id.ivShare);

        mDayPlan = new ArrayList<>();
        adapter = new DayPlanAdapter(mDayPlan);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), HORIZONTAL, false);
        rvSchedule.setLayoutManager(linearLayoutManager);
        rvSchedule.setAdapter(adapter);

        // Circle Indicator
        final PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(rvSchedule);

        Bundle bundle = getArguments();
        if (bundle != null) {
            trip = (Trip) bundle.getSerializable("Trip");
            if (trip != null) {
                tvTripName.setText(trip.getName());

                try {
                    tvUsername.setText(trip.getOwner().fetchIfNeeded().getString("username"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String travelWindow;
                if (trip.getNumDays().intValue() != 1) {
                    travelWindow = trip.getStartDate().toString().substring(0, 10) + " - " + trip.getEndDate().toString().substring(0, 10);
                } else {
                    travelWindow = trip.getStartDate().toString().substring(0, 10);
                }

                tvTravelDates.setText(travelWindow);
                tvDays.setText(trip.getNumDays().toString());

                String costString = "$" + trip.getCost();
                tvCost.setText(costString);

                String budgetString = "$" + trip.getBudget();
                tvBudget.setText(budgetString);

                try {
                    tvCityState.setText(String.format("%s, %s", trip.getCity().fetchIfNeeded().getString("name"), trip.getCity().getState()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Glide.with(Objects.requireNonNull(getContext()))
                        .load(trip.getImage().getUrl())
                        .into(ivCoverPhoto);

                ParseFile image = (ParseFile) trip.getOwner().get("profileImage");
                if (image != null) {
                    Glide.with(getContext())
                            .load(image.getUrl())
                            .apply(RequestOptions.circleCropTransform())
                            .into(ivProfileImage);
                }

                ParseQuery<DayPlan> dayPlanQuery = new ParseQuery<>(DayPlan.class);
                dayPlanQuery.include(DayPlan.KEY_TRIP);
                dayPlanQuery.whereEqualTo(DayPlan.KEY_TRIP, trip);
                dayPlanQuery.addAscendingOrder(DayPlan.KEY_DATE);
                dayPlanQuery.findInBackground(new FindCallback<DayPlan>() {
                    @Override
                    public void done(List<DayPlan> dayPlans, ParseException e) {
                        if (e == null) {
                            adapter.clear();
                            mDayPlan.addAll(dayPlans);
                            addCircleIndicator(view, rvSchedule, pagerSnapHelper);
                        } else {
                            e.printStackTrace();
                            showAlertDialog();
                        }
                    }
                });

                addOnClickListeners();
            }
        }
    }

    private void addOnClickListeners() {

        // Opens the city location in Google Maps when clicked
        tvCityState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMaps(trip);
            }
        });

        // Opens the city location in Google Maps when clicked
        ivPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMaps(trip);
            }
        });

        // Allows the User to view another User's profile
        tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = (String) tvUsername.getText();
                Fragment fragment = new ProfileFragment();
                Bundle userBundle = new Bundle();
                userBundle.putString("username", username);
                fragment.setArguments(userBundle);

                if (username.equals(ParseUser.getCurrentUser().getUsername())) {
                    MainActivity.bottomNavigationView.setSelectedItemId(R.id.action_profile);
                }

                MainActivity.fragmentManager.beginTransaction()
                        .replace(R.id.flContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Allows User to share a text message containing the City, start date, and end date of their trip
        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = null;
                try {
                    text = "Check out this awesome trip! I'm going to " + trip.getCity().fetchIfNeeded().getString("name");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                text += " from " + trip.getStartDate().toString() + " to " + trip.getEndDate().toString() + "!";
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Share Trip"));
            }
        });
    }

    private void addCircleIndicator(@NonNull View view, RecyclerView rvSchedule, PagerSnapHelper pagerSnapHelper) {
        CircleIndicator2 indicator = view.findViewById(R.id.indicator);
        indicator.attachToRecyclerView(rvSchedule, pagerSnapHelper);
        adapter.registerAdapterDataObserver(indicator.getAdapterDataObserver());
    }

    private void openMaps(Trip trip) {
        String location = geoPointToString((Objects.requireNonNull(trip.getCity().get("location"))).toString());
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        String data = String.format("%s?q=%s", location, trip.getCity().getName());
        intent.setData(Uri.parse(data));
        Objects.requireNonNull(getContext()).startActivity(intent);
    }

    private String geoPointToString(String geoPoint) {
        String temp = geoPoint.substring(geoPoint.indexOf('[') + 1, geoPoint.length() - 1);
        return "geo:" + temp;
    }

    private void showAlertDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Error loading trip details.")
                .setPositiveButton("OK", null)
                .create();
        dialog.show();
    }
}
