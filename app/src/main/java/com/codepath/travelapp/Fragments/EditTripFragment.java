package com.codepath.travelapp.Fragments;

import android.content.DialogInterface;
import android.graphics.Color;
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
import com.codepath.travelapp.Models.Trip;
import com.codepath.travelapp.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator2;

public class EditTripFragment extends Fragment {

    private androidx.appcompat.widget.Toolbar toolbar;
    private TextView etEditTripName;
    private Button saveBtn;
    private Button deleteBtn;

    private String tripName;
    private City city;
    private String startDate;
    private String endDate;
    private int budget;
    private int numDays;

    private int tripCost;
    private ArrayList<Event> allAvailableEvents;
    private ArrayList<DayPlan> dayPlans;

    private int originalTripCost;
    private ArrayList<Event> originalAllAvailableEvents;
    private ArrayList<DayPlan> originalDayPlans;

    private Bundle bundle;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        MainActivity.bottomNavigationView.setVisibility(View.GONE);
        return inflater.inflate(R.layout.fragment_edit_trip, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etEditTripName = view.findViewById(R.id.etEditTripName);
        //TextView tvTravelDates = view.findViewById(R.id.tvTravelDatesEditFragment);
        TextView tvDays = view.findViewById(R.id.tvDaysEditFragment);
        TextView tvBudget = view.findViewById(R.id.tvBudgetEditFragment);
        TextView tvTripCost = view.findViewById(R.id.tvCostEditFragment);
        TextView tvRemainingBudget = view.findViewById(R.id.tvRemainingBudgetEditFragment);
        TextView tvCityState = view.findViewById(R.id.tvCityStateEditFragment);
        saveBtn = view.findViewById(R.id.saveBtn);
        deleteBtn = view.findViewById(R.id.deleteBtn);
        RecyclerView rvSchedule = view.findViewById(R.id.rvEditableSchedule);

        bundle = getArguments();
        if (bundle == null) {
            Log.d("EditTrip Fragment", "No bundle");
            showAlertDialog("Error loading page");
        }

        if (bundle.getString("return_screen").contentEquals("review")) {
            deleteBtn.setVisibility(View.GONE);
        } else if (bundle.getString("return_screen").contentEquals("details")) {
            deleteBtn.setVisibility(View.VISIBLE);
        }

        tripName = bundle.getString("trip_name");
        city = bundle.getParcelable("city");
        startDate = bundle.getString("start_date");
        endDate = bundle.getString("end_date");
        numDays = bundle.getInt("number_days");
        budget = bundle.getInt("budget");
        tripCost = bundle.getInt("trip_cost");
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

        originalTripCost = getTripCost(originalDayPlans);

        // Fill in layout
        toolbar = view.findViewById(R.id.tbEditProfile);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitle("Edit Trip");

        etEditTripName.setText(tripName);

//        String travelWindow;
//        if (numDays != 1) {
//            travelWindow = startDate.substring(0, 10) + " - " +
//                    endDate.substring(0, 10);
//        } else {
//            travelWindow = startDate.substring(0, 10);
//        }
//
//        tvTravelDates.setText(travelWindow);
        tvDays.setText(String.valueOf(numDays));
        tvBudget.setText(String.format("$%s", String.valueOf(budget)));
        tvTripCost.setText(String.format("$%s", String.valueOf(tripCost)));
        tvCityState.setText(String.format("%s, %s", city.getName(), city.getState()));

        // Updates amount remaining
        int remainingMoney = budget-tripCost;
        tvRemainingBudget.setText(String.format("$%s", String.valueOf(remainingMoney)));
        if (remainingMoney < 0) {
            tvRemainingBudget.setTextColor(Color.RED);
        } else if (remainingMoney > 0) {
            tvRemainingBudget.setTextColor(getResources().getColor(R.color.LightSkyBlue));
        }

        // Populate DayPlans
        DayPlanEditableAdapter dayPlanEditableAdapterAdapter = new DayPlanEditableAdapter(dayPlans,
                allAvailableEvents, budget, remainingMoney, tvTripCost, tvRemainingBudget);
        rvSchedule.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL,
                false));
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
                                // Returns to trip review page
                                if (bundle.getString("return_screen").contentEquals("review")) {
                                    Fragment fragment = new TripReviewFragment();
                                    // Overwrites pre-existing "available_events" and "dayPlans"
                                    // in the bundle
                                    bundle.putParcelableArrayList("available_events", originalAllAvailableEvents);
                                    bundle.putParcelableArrayList("dayPlans", originalDayPlans);
                                    bundle.putInt("trip_cost", originalTripCost);
                                    fragment.setArguments(bundle);
                                    MainActivity.fragmentManager.beginTransaction()
                                            .replace(R.id.flContainer, fragment)
                                            .commit();
                                } else if (bundle.getString("return_screen").contentEquals("details")) {
                                    Fragment fragment = new TripDetailsFragment();
                                    fragment.setArguments(bundle);
                                    MainActivity.fragmentManager.beginTransaction()
                                            .replace(R.id.flContainer, fragment)
                                            .commit();
                                }
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
                // Returns to review fragment
                if (bundle.getString("return_screen").contentEquals("review")) {
                    Fragment fragment = new TripReviewFragment();
                    bundle.putString("trip_name", etEditTripName.getText().toString());
                    bundle.putInt("trip_cost", getTripCost(dayPlans));
                    bundle.putParcelableArrayList("dayPlans", dayPlans);
                    bundle.putParcelableArrayList("available_event", allAvailableEvents);
                    fragment.setArguments(bundle);
                    MainActivity.fragmentManager.beginTransaction()
                            .replace(R.id.flContainer, fragment)
                            .commit();
                    Toast.makeText(getContext(), "Your trip has been updated", Toast.LENGTH_LONG).show();
                } else if (bundle.getString("return_screen").contentEquals("details")) {
                    // Return to details fragment
                    final Fragment fragment = new TripDetailsFragment();
                    final Trip trip = bundle.getParcelable("Trip");
                    trip.setName(etEditTripName.getText().toString());
                    trip.setCost(getTripCost(dayPlans));
                    bundle.putParcelable("Trip", trip);
                    trip.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                ParseQuery<DayPlan> dayPlanQuery = new ParseQuery<>(DayPlan.class);
                                dayPlanQuery.include(DayPlan.KEY_TRIP);
                                dayPlanQuery.whereEqualTo(DayPlan.KEY_TRIP, trip);
                                dayPlanQuery.findInBackground(new FindCallback<DayPlan>() {
                                    @Override
                                    public void done(List<DayPlan> oldDayPlans, ParseException e) {
                                        if (e == null) {
                                            // Removes the old Day Plans
                                            for (int day = 0; day < numDays; day++) {
                                                DayPlan currDayPlan = oldDayPlans.get(day);
                                                currDayPlan.deleteInBackground();
                                            }
                                            if (dayPlans != null) {
                                                for (int i = 0; i < dayPlans.size(); i++) {
                                                    if (i != dayPlans.size() - 1) {
                                                        dayPlans.get(i).setTrip(trip);
                                                        dayPlans.get(i).saveInBackground();
                                                    } else {
                                                        dayPlans.get(i).setTrip(trip);
                                                        dayPlans.get(i).saveInBackground(new SaveCallback() {
                                                            @Override
                                                            public void done(ParseException e) {
                                                                if (e == null) {
                                                                    fragment.setArguments(bundle);
                                                                    MainActivity.fragmentManager.beginTransaction()
                                                                            .replace(R.id.flContainer, fragment)
                                                                            .commit();
                                                                } else {
                                                                    showAlertDialog("Error saving trip");
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        } else {
                                            showAlertDialog("Error saving trip");
                                        }
                                    }
                                });
                            } else {
                                showAlertDialog("Error saving trip");
                            }
                        }
                    });
                }
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Are you sure you want to delete this trip?")
                        .setMessage("This process is irreversible.")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteTrip();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }
        });
    }

    private int getTripCost(ArrayList<DayPlan> dayPlansList) {
        int runningCost = 0;
        for (int i = 0; i < dayPlansList.size(); i++) {
            DayPlan currday = dayPlansList.get(i);
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

    private void deleteTrip() {
        // Deletes each dayPlan
        for (int day = 0; day < numDays; day++) {
            if (day != numDays - 1) {
                DayPlan dayPlan = originalDayPlans.get(day);
                dayPlan.deleteInBackground();
            } else {
                DayPlan dayPlan = originalDayPlans.get(day);
                dayPlan.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Trip trip = bundle.getParcelable("Trip");
                            trip.deleteInBackground(new DeleteCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        MainActivity.bottomNavigationView.setSelectedItemId(R.id.action_home);
                                        Fragment fragment = new TimelineFragment();
                                        MainActivity.fragmentManager.beginTransaction()
                                                .replace(R.id.flContainer, fragment)
                                                .commit();
                                    } else {
                                        e.printStackTrace();
                                        showAlertDialog("Error deleting trip.");
                                    }
                                }
                            });
                        } else {
                            e.printStackTrace();
                            showAlertDialog("Error deleting trip.");
                        }
                    }
                });
            }
        }
    }

    private void showAlertDialog(String message) {
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(getContext())
                .setTitle(message)
                .setPositiveButton("OK", null)
                .create();
        dialog.show();
    }
}
