package com.codepath.travelapp.fragments;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.codepath.travelapp.Adapters.TagSelectedAdapter;
import com.codepath.travelapp.MainActivity;
import com.codepath.travelapp.Models.City;
import com.codepath.travelapp.Models.Tag;
import com.codepath.travelapp.Models.Trip;
import com.codepath.travelapp.R;
import com.parse.ParseUser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL;
import static java.time.temporal.ChronoUnit.DAYS;

public class TripReviewFragment extends Fragment {

    private final String TAG = "TripReviewFragment";

    private TagSelectedAdapter adapter;

    private ImageView ivCoverPhoto;
    private TextView tvTripName;
    private TextView tvTravelDates;
    private TextView tvDays;
    private TextView tvBudget;
    private RecyclerView rvTags;
    private RecyclerView rvSchedule;
    private Button btnAccept;
    private Button btnDeny;

    private String tripName;
    private City city;
    private String startDate;
    private String endDate;
    private String budget;
    private ArrayList<Tag> tags;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trip_review,container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivCoverPhoto = view.findViewById(R.id.ivCoverPhoto);
        tvTripName = view.findViewById(R.id.tvTripName);
        tvTravelDates = view.findViewById(R.id.tvTravelDates);
        tvDays = view.findViewById(R.id.tvDays);
        tvBudget = view.findViewById(R.id.tvBudget);
        rvTags = view.findViewById(R.id.rvTags);
        rvSchedule = view.findViewById(R.id.rvSchedule);
        btnAccept = view.findViewById(R.id.btnAccept);
        btnDeny = view.findViewById(R.id.btnDeny);

        Bundle bundle = getArguments();

        if (bundle != null) {
            tripName = bundle.getString("trip_name");
            city = bundle.getParcelable("city");
            startDate = bundle.getString("start_date");
            endDate = bundle.getString("end_date");
            budget = bundle.getString("budget");
            tags = bundle.getParcelableArrayList("selected_tags");
        }

        final int numDays = (int) getDifferenceDays(getParseDate(startDate),getParseDate(endDate));

        tvTripName.setText(tripName);
        tvTravelDates.setText(startDate + " - " + endDate);
        tvDays.setText("" + numDays);
        tvBudget.setText(budget);

        adapter = new TagSelectedAdapter(tags);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), HORIZONTAL, false);
        rvTags.setLayoutManager(linearLayoutManager);
        rvTags.setAdapter(adapter);
        Glide.with(getContext())
                .load(city.getImage().getUrl())
                .into(ivCoverPhoto);


        btnAccept.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.O)
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                Trip trip = new Trip();
                trip.setOwner(ParseUser.getCurrentUser());
                trip.setName(tripName);
                // trip.setCity();
                trip.setStartDate(getParseDate(startDate));
                trip.setEndDate(getParseDate(endDate));
                trip.setNumDays(numDays);
                trip.setBudget(Integer.parseInt(budget));

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
    private static LocalDate getParseDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        return LocalDate.parse(date, formatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static long getDifferenceDays(LocalDate d1, LocalDate d2) {
        long diff = DAYS.between(d1, d2);
        return diff;
    }
}
