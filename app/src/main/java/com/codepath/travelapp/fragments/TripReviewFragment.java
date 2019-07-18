package com.codepath.travelapp.fragments;

import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.travelapp.MainActivity;
import com.codepath.travelapp.Models.Trip;
import com.codepath.travelapp.R;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;

public class TripReviewFragment extends Fragment {

    private final String TAG = "TripReviewFragment";

    private ImageView ivCoverPhoto;
    private TextView tvTripName;
    private TextView tvTravelDates;
    private TextView tvDays;
    private TextView tvBudget;
    private RecyclerView rvSchedule;
    private Button btnAccept;
    private Button btnDeny;

    private String tripName;
    private String city;
    private String startDate;
    private String endDate;
    private String budget;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trip_review,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivCoverPhoto = view.findViewById(R.id.ivCoverPhoto);
        tvTripName = view.findViewById(R.id.tvTripName);
        tvTravelDates = view.findViewById(R.id.tvTravelDates);
        tvDays = view.findViewById(R.id.tvDays);
        tvBudget = view.findViewById(R.id.tvBudget);
        rvSchedule = view.findViewById(R.id.rvSchedule);
        btnAccept = view.findViewById(R.id.btnAccept);
        btnDeny = view.findViewById(R.id.btnDeny);

        Bundle bundle = getArguments();

        if (bundle != null) {
            tripName = bundle.getString("trip_name");
            city = bundle.getString("city");
            startDate = bundle.getString("start_date");
            endDate = bundle.getString("end_date");
            budget = bundle.getString("budget");
        }

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Trip trip = new Trip();
                trip.setOwner(ParseUser.getCurrentUser());
                trip.setName(tripName);
                // trip.setCity();
                // trip.setStartDate();
                // trip.setEndDate(endDate);
                // trip.setNumDays();
                // trip.setBudget(budget);

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Date getDateFromString(String string) {
        DateFormat formatter = new DateFormat() {
            @NonNull
            @Override
            public StringBuffer format(@NonNull Date date, @NonNull StringBuffer stringBuffer, @NonNull FieldPosition fieldPosition) {
                return null;
            }

            @Nullable
            @Override
            public Date parse(@NonNull String s, @NonNull ParsePosition parsePosition) {
                return null;
            }
        };

        Date dateObject;

        try{
            dateObject = formatter.parse(string);
            return dateObject;
        }

        catch (java.text.ParseException e)
        {
            e.printStackTrace();
            Log.i("E11111111111", e.toString());
        }
        return new Date();
    }
}
