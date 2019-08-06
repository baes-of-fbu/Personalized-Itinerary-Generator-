package com.codepath.travelapp.Fragments;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travelapp.Activities.MainActivity;
import com.codepath.travelapp.Adapters.TagGridAdapter;
import com.codepath.travelapp.Models.City;
import com.codepath.travelapp.Models.DayPlan;
import com.codepath.travelapp.Models.Event;
import com.codepath.travelapp.Models.Tag;
import com.codepath.travelapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static java.time.temporal.ChronoUnit.DAYS;

public class ComposeFragment extends Fragment {

    private static final int NUM_COLUMNS = 3;
    private static final int NUM_TAGS = 20;
    private static final String KEY_MORNING = "morning";
    private static final String KEY_AFTERNOON = "afternoon";
    private static final String KEY_EVENING = "evening";

    private EditText etTripName;
    private EditText etStartDate;
    private EditText etEndDate;
    private EditText etBudget;
    private Spinner spCity;
    private Button btnGenerate;

    private int numDays;
    private String tripName;
    private String startDate;
    private String endDate;
    private int budget;
    private String cityName;
    private int tripCost;

    private City city;

    private TagGridAdapter adapter;
    private List<Tag> allTags;
    private ArrayList<Tag> selectedTags;
    private ArrayList<Event> allAvailableEvents;
    private ArrayList<DayPlan> dayPlans;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
        return inflater.inflate(R.layout.fragment_compose,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etTripName = view.findViewById(R.id.etTripName);
        etStartDate = view.findViewById(R.id.etStartDate);
        etEndDate = view.findViewById(R.id.etEndDate);
        etBudget = view.findViewById(R.id.etBudget);
        spCity = view.findViewById(R.id.spCity);
        btnGenerate = view.findViewById(R.id.btnGenerate);

        populateGridView(view);
        addOnClickListeners();
    }

    // Queries for all Tags in Parse database and populates the GridView
    private void populateGridView(View view) {
        final RecyclerView rvTags = view.findViewById(R.id.rvTags);
        ParseQuery<Tag> tagQuery = new ParseQuery<>(Tag.class);
        tagQuery.setLimit(NUM_TAGS);
        tagQuery.findInBackground(new FindCallback<Tag>() {
            @Override
            public void done(List<Tag> tagList, ParseException e) {
                if (e == null) {
                    allTags = tagList;
                    adapter = new TagGridAdapter(allTags);
                    rvTags.setAdapter(adapter);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),
                            GridLayoutManager.VERTICAL);
                    gridLayoutManager.setSpanCount(NUM_COLUMNS);
                    rvTags.setLayoutManager(gridLayoutManager);
                } else {
                    e.printStackTrace();
                    showAlertDialog();
                }
            }
        });
    }

    // Adds onClickListeners for etStartDate, etEndDate, and btnGenerate
    private void addOnClickListeners(){

        etStartDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                getCurrentDate(etStartDate);
            }
        });

        etEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentDate(etEndDate);
            }
        });

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.O)
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                tripName = etTripName.getText().toString();
                startDate = etStartDate.getText().toString();
                endDate = etEndDate.getText().toString();

                cityName = spCity.getSelectedItem().toString();

                // Checks input fields to prevent incorrect user input
                if (tripName.length() == 0) {
                    Toast.makeText(getContext(), "Specify trip name", Toast.LENGTH_LONG)
                            .show();
                } else if (startDate.length() == 0) {
                    Toast.makeText(getContext(), "Specify start date", Toast.LENGTH_LONG)
                            .show();
                } else if (endDate.length() == 0) {
                    Toast.makeText(getContext(), "Specify end date", Toast.LENGTH_LONG)
                            .show();
                } else {
                    numDays = (int) getDifferenceBetweenDays(TripReviewFragment
                            .getParseDate(startDate), TripReviewFragment.getParseDate(endDate));
                    if (numDays < 1) {
                        Toast.makeText(getContext(),
                                "Invalid dates. Please fix your start and/or end date",
                                Toast.LENGTH_LONG).show();
                    } else if (dateHasPassed(TripReviewFragment.getParseDate(startDate))) {
                        Toast.makeText(getContext(), "Start date has already passed",
                                Toast.LENGTH_SHORT).show();
                    } else if (etBudget.getText().toString().length() == 0) {
                        Toast.makeText(getContext(), "Specify budget",
                                Toast.LENGTH_LONG).show();
                    } else {
                        budget = Integer.parseInt(etBudget.getText().toString());
                        if (budget <= 0) {
                            Toast.makeText(getContext(), "Minimum budget is $0.",
                                    Toast.LENGTH_LONG).show();
                        } else if (cityName.contains("Select city")) {
                            Toast.makeText(getContext(), "Select city",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            selectedTags = adapter.getSelectedTags();
                            generateSchedule(cityName);
                        }
                    }
                }
            }
        });
    }

    // Generates schedule and opens review fragment
    private void generateSchedule(final String cityName) {
        allAvailableEvents = new ArrayList<>();

        ParseQuery<City> cityQuery = new ParseQuery<>(City.class);
        cityQuery.whereEqualTo(City.KEY_NAME, cityName);
        cityQuery.findInBackground(new FindCallback<City>() {
            @Override
            public void done(List<City> cityList, ParseException e) {
                if (e == null) {
                    city = cityList.get(0);
                    // Creates a new query for the events in the city
                    List<ParseQuery<Event>> queries = new ArrayList<>();

                    // Creates queries for each tag
                    for (int i = 0; i < selectedTags.size(); i++) {
                        Tag tag = selectedTags.get(i);
                        ParseQuery<Event> eventQuery = tag.getEventsRelation().getQuery();
                        eventQuery.whereEqualTo("city", city);
                        queries.add(eventQuery);
                    }

                    // Sends all tag queries in one main query
                    ParseQuery<Event> mainQuery = ParseQuery.or(queries);
                    mainQuery.include("name");
                    mainQuery.findInBackground(new FindCallback<Event>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void done(List<Event> eventList, ParseException e) {
                            if (e == null) {
                                allAvailableEvents.addAll(eventList);
                                createDayPlans();
                                sendBundle();
                            } else {
                                e.printStackTrace();
                                showAlertDialog();
                            }
                        }
                    });
                } else {
                    e.printStackTrace();
                    showAlertDialog();
                }
            }
        });
    }

    // Adds events to day plans
    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void createDayPlans() {
        allAvailableEvents.addAll(shuffleEvents());
        dayPlans = new ArrayList<>();
        int runningBudget = budget;

        for (int day = 0; day < numDays; day++) {
            // Create a DayPlan for each day in the Trip and assign it the appropriate calendar Date
            DayPlan tempDay = new DayPlan();
            Date date = changeToDate(TripReviewFragment.getParseDate(startDate), day);
            tempDay.setDate(date);

            // Picks morning event and updates budget
            Event morningEvent = getEvent(KEY_MORNING, runningBudget);
            if (morningEvent != null) {
                allAvailableEvents.remove(morningEvent);
                runningBudget -= (int) morningEvent.getCost();
                tempDay.setMorningEvent(morningEvent);
            }

            // Picks afternoon event and updates budget
            Event afternoonEvent = getEvent(KEY_AFTERNOON, runningBudget);
            if (afternoonEvent != null) {
                allAvailableEvents.remove(afternoonEvent);
                runningBudget -= (int) afternoonEvent.getCost();
                tempDay.setAfternoonEvent(afternoonEvent);
            }

            // Picks evening event and updates budget
            Event eveningEvent = getEvent(KEY_EVENING, runningBudget);
            if (eveningEvent != null) {
                allAvailableEvents.remove(eveningEvent);
                runningBudget -= (int) eveningEvent.getCost();
                tempDay.setEveningEvent(eveningEvent);
            }

            tempDay.saveInBackground();
            dayPlans.add(tempDay);
        }

        tripCost = budget - runningBudget;
    }

    private Event getEvent(String timeOfDay, int runningBudget) {
        int numEvents = allAvailableEvents.size();
        for (int i = 0; i < numEvents; i++) {
            Event event = allAvailableEvents.get(i);
            if (isEventAvailable(event, timeOfDay) && isEventWithinBudget(event, runningBudget)) {
                // Returns event if it is available and within budget
                return event;
            }
        }
        // Returns null if no event was found
        return null;
    }

    // Events are shuffled so that each trip that is made can have a unique order of events
    private ArrayList<Event> shuffleEvents() {
        int numEvents = allAvailableEvents.size();
        ArrayList<Event> shuffledEvents = new ArrayList<>();
        for (int i = 0; i < numEvents; i++) {
            Event event = getRandomElement(allAvailableEvents);
            // Adds the random event to the shuffledEvents list
            shuffledEvents.add(event);
            // Removes the event from allAvailableEvents
            allAvailableEvents.remove(event);
        }
        return shuffledEvents;
    }

    // Returns a random event from a list of events
    private Event getRandomElement(List<Event> list) {
        Random rand = new Random();
        if (list.size() == 0) {
            return null;
        }
        return list.get(rand.nextInt(list.size()));
    }

    // Returns if the event is available during a given time of day
    private Boolean isEventAvailable(Event event, String timeOfDay) {
        if (timeOfDay.contentEquals(KEY_MORNING)) {
            return event.isAvailableMorning();
        } else if (timeOfDay.contentEquals(KEY_AFTERNOON)) {
            return event.isAvailableAfternoon();
        } else if (timeOfDay.contentEquals(KEY_EVENING)) {
            return event.isAvailableEvening();
        }
        return false;
    }

    // Checks if the cost of an event is within the budget
    private Boolean isEventWithinBudget(Event event, int budget) {
        int eventCost = (int) event.getCost();
        return eventCost < budget;
    }

    // Sends information to review fragment
    private void sendBundle() {
        Fragment fragment = new TripReviewFragment();

        Bundle bundle = new Bundle();
        bundle.putString("trip_name", etTripName.getText().toString());
        bundle.putString("start_date", etStartDate.getText().toString());
        bundle.putString("end_date", etEndDate.getText().toString());
        bundle.putInt("number_days", numDays);
        bundle.putInt("budget", budget);
        bundle.putInt("trip_cost", tripCost);
        bundle.putParcelableArrayList("selected_tags", selectedTags);
        bundle.putParcelable("city", city);
        bundle.putParcelableArrayList("dayPlans", dayPlans);
        bundle.putParcelableArrayList("available_events", allAvailableEvents);
        fragment.setArguments(bundle);

        MainActivity.fragmentManager.beginTransaction()
                .replace(R.id.flContainer, fragment)
                .commit();
        MainActivity.bottomNavigationView.setVisibility(View.GONE);
    }

    // Opens a calendar and fills textView with selected date
    private void getCurrentDate(final TextView tvDate) {
        DatePickerDialog datePickerDialog =
                new DatePickerDialog(Objects.requireNonNull(getContext()),
                        new DatePickerDialog.OnDateSetListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                tvDate.setText(simpleDateFormat.format(calendar.getTime()));
            }

        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    // Returns number of days between two dates
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static long getDifferenceBetweenDays(LocalDate d1, LocalDate d2) {
        long diff = DAYS.between(d1, d2);
        return diff + 1;
    }

    // Reformat a LocalDate object to a Date object
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static Date changeToDate(LocalDate d1, int numDays) {
        return java.sql.Date.valueOf((DAYS.addTo(d1, numDays)).toString());
    }

    // Prevents a user from creating a trip with a start date that has already passed
    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.N)
    private Boolean dateHasPassed(LocalDate startDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String currDate = simpleDateFormat.format(android.icu.util.Calendar.getInstance()
                .getTime());
        LocalDate todayDate = TripReviewFragment.getParseDate(currDate);

        int numDays = (int) getDifferenceBetweenDays(todayDate, startDate);
        return (numDays < 1);
    }

    private void showAlertDialog() {
        AlertDialog dialog = new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                .setTitle("Error with compose.")
                .setPositiveButton("OK", null)
                .create();
        dialog.show();
    }
}
