package com.codepath.travelapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.codepath.travelapp.Activities.MainActivity;
import com.codepath.travelapp.Models.City;
import com.codepath.travelapp.Models.DayPlan;
import com.codepath.travelapp.Models.Tag;
import com.codepath.travelapp.R;
import com.parse.ParseFile;

import java.util.ArrayList;

public class EditTripFragment extends Fragment {

    private androidx.appcompat.widget.Toolbar toolbar;
    private TextView etEditTripName;
    private Button saveBtn;

    private String tripName;

    private Bundle bundle;

    // TODO USE THESE
    private City city;
    private String startDate;
    private String endDate;
    private int budget;
    private int numDays;
    private ArrayList<Tag> tags;
    private ArrayList<DayPlan> dayPlans;
    private ParseFile image;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_trip,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etEditTripName = view.findViewById(R.id.etEditTripName);
        saveBtn = view.findViewById(R.id.saveBtn);


        toolbar = view.findViewById(R.id.tbEditProfile);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitle("Edit Trip");


        bundle = getArguments();
        if (bundle != null) {
            // TODO CHANGE WHY THE BUNDLE ISN'T PASSING
            tripName = bundle.getString("trip_name");
//            city = bundle.getParcelable("city");
//            startDate = bundle.getString("start_date");
//            endDate = bundle.getString("end_date");
//            numDays = bundle.getInt("number_days");
//            budget = bundle.getInt("budget");
//            tags = bundle.getParcelableArrayList("selected_tags");
//            dayPlans = bundle.getParcelableArrayList("dayPlans");


            etEditTripName.setText(tripName);
        }


        addOnClickListeners();
    }

    private void addOnClickListeners() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveAlertDialogFragment saveAlertDialog = SaveAlertDialogFragment.newInstance("Saving Item");
                saveAlertDialog.show(MainActivity.fragmentManager, "fragment_alert");

                save();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
    }


    private void save() {
        Fragment fragment = new TripReviewFragment();
        bundle.putString("trip_name", etEditTripName.getText().toString());
        fragment.setArguments(bundle);
        MainActivity.fragmentManager.beginTransaction()
                .replace(R.id.flContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}
