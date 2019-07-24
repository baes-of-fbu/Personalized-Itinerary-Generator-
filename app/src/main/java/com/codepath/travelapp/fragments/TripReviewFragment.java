package com.codepath.travelapp.fragments;

import android.annotation.TargetApi;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.codepath.travelapp.Adapters.DayPlanAdapter;
import com.codepath.travelapp.Adapters.TagSelectedAdapter;
import com.codepath.travelapp.GravitySnapHelper;
import com.codepath.travelapp.MainActivity;
import com.codepath.travelapp.Models.City;
import com.codepath.travelapp.Models.DayPlan;
import com.codepath.travelapp.Models.Tag;
import com.codepath.travelapp.Models.Trip;
import com.codepath.travelapp.R;
import com.parse.ParseUser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

import static androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL;

public class TripReviewFragment extends Fragment {

    private String tripName;
    private City city;
    private String startDate;
    private String endDate;
    private int budget;
    private int numDays;
    private ArrayList<Tag> tags;
    private ArrayList<DayPlan> dayPlans;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trip_review,container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            tripName = bundle.getString("trip_name");
            city = bundle.getParcelable("city");
            startDate = bundle.getString("start_date");
            endDate = bundle.getString("end_date");
            numDays = bundle.getInt("number_days");
            budget = bundle.getInt("budget");
            tags = bundle.getParcelableArrayList("selected_tags");
            dayPlans = bundle.getParcelableArrayList("dayPlans");
        }

        ImageView ivCoverPhoto = view.findViewById(R.id.ivCoverPhoto);
        TextView tvTripName = view.findViewById(R.id.tvTripName);
        TextView tvTravelDates = view.findViewById(R.id.tvTravelDates);
        TextView tvDays = view.findViewById(R.id.tvDays);
        TextView tvBudget = view.findViewById(R.id.tvBudget);
        RecyclerView rvTags = view.findViewById(R.id.rvTags);
        RecyclerView rvSchedule = view.findViewById(R.id.rvSchedule);
        Button btnAccept = view.findViewById(R.id.btnAccept);
        Button btnDeny = view.findViewById(R.id.btnDeny);

        SnapHelper snapHelper = new GravitySnapHelper(Gravity.START);
        snapHelper.attachToRecyclerView(rvTags);

        tvTripName.setText(tripName);
        tvTravelDates.setText(startDate + " - " + endDate);
        tvDays.setText("" + numDays);
        tvBudget.setText(String.valueOf(budget));

        // Populate list of Tags
        TagSelectedAdapter adapter = new TagSelectedAdapter(tags);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), HORIZONTAL, false);
        rvTags.setLayoutManager(linearLayoutManager);
        rvTags.setAdapter(adapter);
        Glide.with(Objects.requireNonNull(getContext()))
                .load(city.getImage().getUrl())
                .into(ivCoverPhoto);

        // Populate DayPlans
        DayPlanAdapter dayPlanAdapter = new DayPlanAdapter(dayPlans);
        rvSchedule.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rvSchedule.setAdapter(dayPlanAdapter);



        btnAccept.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.O)
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                Trip trip = new Trip();
                trip.setOwner(ParseUser.getCurrentUser());
                trip.setName(tripName);
                trip.setCity(city);
                trip.setStartDate(getParseDate(startDate));
                trip.setEndDate(getParseDate(endDate));
                trip.setNumDays(numDays);
                trip.setBudget(budget);

                Fragment fragment = new TripDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("Trip", trip);
                fragment.setArguments(bundle);
                MainActivity.fragmentManager.beginTransaction()
                        .replace(R.id.flContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        btnDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new TimelineFragment();

                Bundle bundle = new Bundle();
                fragment.setArguments(bundle);

                MainActivity.fragmentManager.beginTransaction()
                        .replace(R.id.flContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.N)
    static LocalDate getParseDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        return LocalDate.parse(date, formatter);
    }
}
