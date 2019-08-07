package com.codepath.travelapp.Fragments;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.codepath.travelapp.Activities.MainActivity;
import com.codepath.travelapp.Adapters.DayPlanAdapter;
import com.codepath.travelapp.Adapters.TagSelectedAdapter;
import com.codepath.travelapp.GravitySnapHelper;
import com.codepath.travelapp.Models.City;
import com.codepath.travelapp.Models.CityImages;
import com.codepath.travelapp.Models.DayPlan;
import com.codepath.travelapp.Models.Event;
import com.codepath.travelapp.Models.Tag;
import com.codepath.travelapp.Models.Trip;
import com.codepath.travelapp.Models.User;
import com.codepath.travelapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import me.relex.circleindicator.CircleIndicator2;

import static androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL;

public class TripReviewFragment extends Fragment implements
        EditTripDialogFragment.EditTripDialogListener {

    private String tripName;
    private City city;
    private String startDate;
    private String endDate;
    private int budget;
    private int tripCost;
    private int numDays;
    private ArrayList<DayPlan> dayPlans;
    private ArrayList<Event> availableEvents;
    private ParseFile image;
    private Bundle bundle;
    private DayPlanAdapter dayPlanAdapter;

    private Button btnAccept;
    private Button btnEdit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trip_review, container, false);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        dayPlanAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ImageView ivCoverPhoto = view.findViewById(R.id.ivCoverPhoto);
        TextView tvTripName = view.findViewById(R.id.tvTripName);
        TextView tvTravelDates = view.findViewById(R.id.tvTravelDates);
        TextView tvDays = view.findViewById(R.id.tvDays);
        TextView tvBudget = view.findViewById(R.id.tvBudget);
        TextView tvTripCost = view.findViewById(R.id.tvCost);
        TextView tvRemainingBudget = view.findViewById(R.id.tvRemainingBudget);
        TextView tvCityState = view.findViewById(R.id.tvCityState);
        RecyclerView rvTags = view.findViewById(R.id.rvTags);
        RecyclerView rvSchedule = view.findViewById(R.id.rvSchedule);
        btnAccept = view.findViewById(R.id.btnAccept);
        btnEdit = view.findViewById(R.id.btnEdit);
        String travelWindow;

        SnapHelper snapHelper = new GravitySnapHelper(Gravity.END);
        snapHelper.attachToRecyclerView(rvTags);

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Creating your schedule...");
        progressDialog.setTitle("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();

        bundle = getArguments();
        if (bundle == null) {
            showAlertDialog();
        }

        tripName = bundle.getString("trip_name");
        city = bundle.getParcelable("city");
        startDate = bundle.getString("start_date");
        endDate = bundle.getString("end_date");
        numDays = bundle.getInt("number_days");
        budget = bundle.getInt("budget");
        tripCost = bundle.getInt("trip_cost");
        ArrayList<Tag> tags = bundle.getParcelableArrayList("selected_tags");
        dayPlans = bundle.getParcelableArrayList("dayPlans");
        availableEvents = bundle.getParcelableArrayList("available_events");

        if (numDays != 1) {
            travelWindow = startDate.substring(0, 10) + " - " +
                    endDate.substring(0, 10);
        } else {
            travelWindow = startDate.substring(0, 10);
        }

        tvTripName.setText(tripName);
        tvTravelDates.setText(String.format("%s", travelWindow));
        tvDays.setText(String.valueOf(numDays));
        tvBudget.setText(String.format("$%s", String.valueOf(budget)));
        tvTripCost.setText(String.format("$%s", String.valueOf(tripCost)));
        tvCityState.setText(String.format("%s, %s", city.getName(), city.getState()));
        int remainingMoney = budget-tripCost;
        tvRemainingBudget.setText(String.format("$%s", String.valueOf(remainingMoney)));
        if (remainingMoney < 0) {
            tvRemainingBudget.setTextColor(Color.RED);
        } else if (remainingMoney > 0) {
            tvRemainingBudget.setTextColor(getResources().getColor(R.color.green));
        }

        // Populate list of Tags
        TagSelectedAdapter adapter = new TagSelectedAdapter(tags);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), HORIZONTAL,
                false);
        rvTags.setLayoutManager(linearLayoutManager);
        rvTags.setAdapter(adapter);

        // Populate the cover photo with a random city image
        ParseQuery<CityImages> cityImagesQuery = new ParseQuery<>(CityImages.class);
        int pageSize = 10;
        cityImagesQuery.setLimit(pageSize);
        cityImagesQuery.include(CityImages.KEY_IMAGE);
        cityImagesQuery.whereEqualTo(CityImages.KEY_CITY, city);
        cityImagesQuery.findInBackground(new FindCallback<CityImages>() {
            @Override
            public void done(List<CityImages> objects, ParseException e) {
                if (e == null) {
                    CityImages cityImage = getRandomElement(objects);
                    if (cityImage != null) {
                        image = cityImage.getImage();
                        Glide.with(Objects.requireNonNull(getContext()))
                                .load(image.getUrl())
                                .into(ivCoverPhoto);
                    }
                    progressDialog.hide();
                } else {
                    showAlertDialog();
                }
            }
        });

        // Populate DayPlans
        dayPlanAdapter = new DayPlanAdapter(dayPlans);
        rvSchedule.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL,
                false));
        rvSchedule.setAdapter(dayPlanAdapter);

        // Circle Indicator
        final PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(rvSchedule);
        CircleIndicator2 indicator = view.findViewById(R.id.indicator);
        indicator.attachToRecyclerView(rvSchedule, pagerSnapHelper);
        dayPlanAdapter.registerAdapterDataObserver(indicator.getAdapterDataObserver());

        addOnClickListeners();
    }

    private void addOnClickListeners() {
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.O)
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                final Trip trip = new Trip();
                trip.setOwner(ParseUser.getCurrentUser());
                trip.setName(tripName);
                trip.setCity(city);
                trip.setStartDate(convertToDate(getParseDate(startDate)));
                trip.setEndDate(convertToDate(getParseDate(endDate)));
                trip.setNumDays(numDays);
                trip.setBudget(budget);
                trip.setImage(image);
                trip.setCost(tripCost);
                trip.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (dayPlans != null) {
                            for (int i = 0; i < dayPlans.size(); i++) {
                                dayPlans.get(i).setTrip(trip);
                                dayPlans.get(i).saveInBackground();
                            }
                        }
                    }
                });

                Fragment fragment = new ProfileFragment();
                Bundle finalBundle = new Bundle();
                finalBundle.putString("username", User.getCurrentUser().getUsername());
                fragment.setArguments(finalBundle);
                MainActivity.bottomNavigationView.setSelectedItemId(R.id.action_profile);
                MainActivity.fragmentManager.beginTransaction()
                        .replace(R.id.flContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = MainActivity.fragmentManager;
                EditTripDialogFragment editTripDialogFragment = EditTripDialogFragment.newInstance();
                editTripDialogFragment.setTargetFragment(TripReviewFragment.this,
                        300);
                editTripDialogFragment.show(fragmentManager, "fragment_edit_trip_options");
            }
        });
    }

    // This is called when the dialog is completed and the results have been passed
    @Override
    public void onFinishEditDialog(String inputText) {
        if (inputText.contentEquals(getString(R.string.edit))) {
            Fragment fragment = new EditTripFragment();
            bundle.putString("return_screen", "edit");
            fragment.setArguments(bundle);
            MainActivity.fragmentManager.beginTransaction()
                    .add(R.id.flContainer, fragment)
                    .commit();
        }

        if (inputText.contentEquals(getString(R.string.delete))) {
            // Deletes each dayPlan
            for (int day = 0; day < numDays; day++) {
                DayPlan dayPlan = dayPlans.get(day);
                dayPlan.deleteInBackground();
            }

            // Returns to the timeline fragment without saving the trip
            Fragment fragment = new TimelineFragment();
            MainActivity.bottomNavigationView.setSelectedItemId(R.id.action_home);
            MainActivity.fragmentManager.beginTransaction()
                    .replace(R.id.flContainer, fragment)
                    .commit();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.N)
    static LocalDate getParseDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        return LocalDate.parse(date, formatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static Date convertToDate(LocalDate d1) {
        return java.sql.Date.valueOf(d1.toString());
    }

    // Returns a random event from a list of events
    private CityImages getRandomElement(List<CityImages> list) {
        Random rand = new Random();
        if (list.size() == 0) {
            return null;
        }
        return list.get(rand.nextInt(list.size()));
    }
    private void showAlertDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Error loading trip review.")
                .setPositiveButton("OK", null)
                .create();
        dialog.show();
    }
}
