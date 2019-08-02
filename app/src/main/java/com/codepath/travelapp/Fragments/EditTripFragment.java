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
import com.codepath.travelapp.Models.Event;
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
    private ArrayList<Event> allAvailableEvents;
    private ArrayList<Event> originalAllAvailableEvents;
    private ArrayList<DayPlan> dayPlans;
    private ArrayList<DayPlan> originalDayPlans;

    private Bundle bundle;
    private ParseFile image;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_trip, container, false);
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
        allAvailableEvents = bundle.getParcelableArrayList("available_events");
        dayPlans = bundle.getParcelableArrayList("dayPlans");

        // Stores the original array lists, which are returned if the user does not click 'save'
        originalAllAvailableEvents = new ArrayList<Event>();
        originalAllAvailableEvents.addAll(allAvailableEvents);
        originalDayPlans = new ArrayList<DayPlan>();

        // Iterates through each dayPlan
        for (int i = 0; i < dayPlans.size(); i++) {
            DayPlan originalDayPlan = new DayPlan();
            DayPlan currDayPlan = dayPlans.get(i);

            // Saves the DayPlan date and trip
            originalDayPlan.setDate(currDayPlan.getDate());

            // Saves a copy of each event with in the day plan
            if (currDayPlan.getMorningEvent() != null) {
                originalDayPlan.setMorningEvent(currDayPlan.getMorningEvent());
            }
            if (currDayPlan.getAfternoonEvent() != null) {
                originalDayPlan.setAfternoonEvent(currDayPlan.getAfternoonEvent());
            }
            if (currDayPlan.getEveningEvent() != null) {
                originalDayPlan.setEveningEvent(currDayPlan.getEveningEvent());
            }

            // Saves the copy of the Original DayPlan
            originalDayPlans.add(originalDayPlan);
        }

        etEditTripName.setText(tripName);

        // Populate DayPlans
        DayPlanEditableAdapter dayPlanEditableAdapterAdapter = new DayPlanEditableAdapter(dayPlans, allAvailableEvents);
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
                                // Reset to the previous state and return to editTrip Fragment
                                Fragment fragment = new TripReviewFragment();
                                // Overwrites pre-existing "available_events" and "dayPlans" in the bundle
                                bundle.putParcelableArrayList("available_events", originalAllAvailableEvents);
                                bundle.putParcelableArrayList("dayPlans", originalDayPlans);
                                fragment.setArguments(bundle);
                                MainActivity.fragmentManager.beginTransaction()
                                        .replace(R.id.flContainer, fragment)
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
        tripCost = getTripCost();
        bundle.putInt("trip_cost", tripCost);
        bundle.putParcelableArrayList("dayPlans", dayPlans);
        bundle.putParcelableArrayList("available_event", allAvailableEvents);
        fragment.setArguments(bundle);
        MainActivity.fragmentManager.beginTransaction()
                .replace(R.id.flContainer, fragment)
                .commit();
        Toast.makeText(getContext(), "Your trip has been updated", Toast.LENGTH_LONG).show();
    }

    private int getTripCost() {
        int runningCost = 0;
        for (int i = 0; i < dayPlans.size(); i++) {
            DayPlan currday = dayPlans.get(i);
            if (currday.getMorningEvent() != null) {
                runningCost += (int) currday.getMorningEvent().getCost();
            }
            if (currday.getAfternoonEvent() != null) {
                runningCost += (int) currday.getAfternoonEvent().getCost();
            }
            if (currday.getEveningEvent() != null) {
                runningCost += (int) currday.getEveningEvent().getCost();
            }
        }
        return runningCost;
    }
}
