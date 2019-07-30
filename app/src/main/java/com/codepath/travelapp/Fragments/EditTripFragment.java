package com.codepath.travelapp.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travelapp.Activities.MainActivity;
import com.codepath.travelapp.Adapters.DayPlanEditableAdapter;
import com.codepath.travelapp.Models.City;
import com.codepath.travelapp.Models.DayPlan;
import com.codepath.travelapp.Models.Tag;
import com.codepath.travelapp.R;
import com.parse.ParseFile;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator2;

public class EditTripFragment extends Fragment {

    private androidx.appcompat.widget.Toolbar toolbar;
    private TextView etEditTripName;
    private Button saveBtn;

    private String tripName;
    private City city;
    private String startDate;
    private String endDate;
    private int budget;
    private int numDays;
    private int tripCost;
    private ArrayList<Tag> tags;
    private ArrayList<DayPlan> dayPlans;

    private Bundle bundle;

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
        RecyclerView rvSchedule = view.findViewById(R.id.rvEditableSchedule);


        toolbar = view.findViewById(R.id.tbEditProfile);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitle("Edit Trip");


        bundle = getArguments();
        if (bundle == null) {
            Log.d("EditTrip Fragment", "No bundle");
        }

        tripName = bundle.getString("trip_name");
        city = bundle.getParcelable("city");
        startDate = bundle.getString("start_date");
        endDate = bundle.getString("end_date");
        numDays = bundle.getInt("number_days");
        budget = bundle.getInt("budget");
        tripCost = bundle.getInt("trip_cost");
        tags = bundle.getParcelableArrayList("selected_tags");
        dayPlans = bundle.getParcelableArrayList("dayPlans");

        etEditTripName.setText(tripName);



        // Populate DayPlans
        DayPlanEditableAdapter dayPlanEditableAdapterAdapter = new DayPlanEditableAdapter(dayPlans);
        rvSchedule.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvSchedule.setAdapter(dayPlanEditableAdapterAdapter);

        // Circle Indicator
        final PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(rvSchedule);
        CircleIndicator2 indicator = view.findViewById(R.id.indicatorEdit);
        indicator.attachToRecyclerView(rvSchedule, pagerSnapHelper);
        dayPlanEditableAdapterAdapter.registerAdapterDataObserver(indicator.getAdapterDataObserver());

        addOnClickListeners();
    }

    private void addOnClickListeners() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Are you sure you want to return without saving?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Fragment fragment = new TripReviewFragment();
                                fragment.setArguments(bundle);
                                MainActivity.fragmentManager.beginTransaction()
                                        .replace(R.id.flContainer, fragment)
                                        .addToBackStack(null)
                                        .commit();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
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
        Toast.makeText(getContext(), "Your trip has been updated", Toast.LENGTH_LONG).show();
    }
}
