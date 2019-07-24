package com.codepath.travelapp.fragments;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travelapp.Adapters.TagGridAdapter;
import com.codepath.travelapp.MainActivity;
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
    private String budgetString;
    private String cityName;
    private City city;
    private Event emptyEvent;

    private TagGridAdapter adapter;
    private List<Tag> allTags;
    private ArrayList<Tag> selectedTags;
    private ArrayList<Event> allAvailableEvents;
    private ArrayList<DayPlan> dayPlans;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

        // Adds tags in grid recycler view
        populateGridView(view);

        addOnClickListeners();
    }

    private void populateGridView(View view) {
        final RecyclerView rvTags = view.findViewById(R.id.rvTags);
        ParseQuery<Tag> tagQuery = new ParseQuery<>(Tag.class);
        tagQuery.setLimit(NUM_TAGS);
        tagQuery.findInBackground(new FindCallback<Tag>() {
            @Override
            public void done(List<Tag> objects, ParseException e) {
                if (e == null) {
                    Log.d("DEBUG", "Successful query for tags");
                    allTags = objects;
                    // Create adapter passing in the sample user data
                    adapter = new TagGridAdapter(allTags);
                    // Attach the adapter to the recyclerView to populate items
                    rvTags.setAdapter(adapter);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), GridLayoutManager.VERTICAL);
                    gridLayoutManager.setSpanCount(NUM_COLUMNS);
                    // Set layout manager to position the items
                    rvTags.setLayoutManager(gridLayoutManager);
                } else {
                    e.printStackTrace();
                    Log.d("DEBUG", "Fetch timeline error: " + e.toString());
                }
            }
        });
    }

    public void addOnClickListeners(){
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
                budgetString = etBudget.getText().toString();
                cityName = spCity.getSelectedItem().toString();

                // Checks prevent incorrect user input
                if (tripName.length() == 0) {
                    Toast.makeText(getContext(), "Specify trip name", Toast.LENGTH_LONG).show();
                } else if (startDate.length() == 0) {
                    Toast.makeText(getContext(), "Specify start date", Toast.LENGTH_LONG).show();
                } else if (endDate.length() == 0) {
                    Toast.makeText(getContext(), "Specify end date", Toast.LENGTH_LONG).show();
                } else {
                    numDays = (int) getDifferenceDays(TripReviewFragment.getParseDate(startDate), TripReviewFragment.getParseDate(endDate));
                    if (numDays < 1) {
                        Toast.makeText(getContext(), "Invalid dates. Please fix your start and/or end date", Toast.LENGTH_LONG).show();
                    } else if (budgetString.length() == 0) {
                        Toast.makeText(getContext(), "Specify budget", Toast.LENGTH_LONG).show();
                    } else if (Integer.parseInt(budgetString) < 0) {
                        Toast.makeText(getContext(), "Minimum budget is $0.", Toast.LENGTH_LONG).show();
                    } else if (cityName.contains("Select city")) {
                        Toast.makeText(getContext(), "Select city", Toast.LENGTH_LONG).show();
                    } else {
                        selectedTags = adapter.getSelectedTags();
                        // Generates schedule and opens review fragment
                        generateSchedule(cityName, Integer.parseInt(budgetString));
                    }
                }
            }
        });
    }

    // Opens a calendar and fills textView with selected date
    private void getCurrentDate(final TextView tvDate) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getContext()), new DatePickerDialog.OnDateSetListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                tvDate.setText(simpleDateFormat.format(calendar.getTime()));
            }

        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    // Returns number of days between two dates
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static long getDifferenceDays(LocalDate d1, LocalDate d2) {
        long diff = DAYS.between(d1, d2);
        return diff + 1;
    }

    private void generateSchedule(final String cityName, final int budget) {
        // Sends network request for city name
        ParseQuery<City> cityQuery = new ParseQuery<>(City.class);
        cityQuery.whereEqualTo(City.KEY_NAME, cityName);
        cityQuery.findInBackground(new FindCallback<City>() {
            @Override
            public void done(List<City> objects, ParseException e) {
                if (e == null) {
                    city = objects.get(0); // Assumes only one city is associated with the name
                    parseEmptyCity(budget);
                } else {
                    Log.d("ComposeFragment", "Failed to query city: " + e.toString());
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Failed to query city: " + cityName, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Sends network request for the empty event, unique to each city
    private void parseEmptyCity(final int budget) {
        ParseQuery<Event> eventQuery = new ParseQuery<Event>(Event.class);
        eventQuery.whereEqualTo(Event.KEY_NAME, "Empty Event");
        eventQuery.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> objects, ParseException e) {
                if (e == null) {
                    emptyEvent = objects.get(0);
                    parseAllAvailableEvents(budget);
                } else {
                    Log.d("Compose Fragment", "Failed to query empty city");
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Sends network requests for all events in a city, based off the tags
    private void parseAllAvailableEvents(final int budget) {

        allAvailableEvents = new ArrayList<>();
        List<ParseQuery<Event>> queries = new ArrayList<ParseQuery<Event>>();

        // Create queries for each tag
        for (int i = 0; i < selectedTags.size(); i++) {
            Tag tag = selectedTags.get(i);
            ParseQuery<Event> eventQuery = tag.getEventsRelation().getQuery();
            eventQuery.whereEqualTo("city", city);
            queries.add(eventQuery);
        }

        // Send all queries in one main query
        ParseQuery<Event> mainQuery = ParseQuery.or(queries);
        mainQuery.findInBackground(new FindCallback<Event>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void done(List<Event> objects, ParseException e) {
                if (e == null) {
                    Log.d("ComposeFragment", "All events" + objects.toString());
                    Toast.makeText(getContext(), "Query for available events successful", Toast.LENGTH_LONG).show();
                    allAvailableEvents.addAll(objects);
                    //parseEventsByTime(Event.KEY_MORNING, budget);
                    createDayPlans(budget);
                    sendBundle(budget);
                } else {
                    Log.d("Compose Fragment", e.toString());
                    Toast.makeText(getContext(), "Query for available events unsuccessful", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    // Adds events to day plans
    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void createDayPlans(int runningBudget) {
        dayPlans = new ArrayList<>();

        // Loop through each day
        for (int day = 0; day < numDays; day++) {

            DayPlan tempDay = new DayPlan();

            Date date = addToDate(TripReviewFragment.getParseDate(startDate), day);
            tempDay.setDate(date);

            // Picks morning event and updates budget
            Event morningEvent = getEvent(KEY_MORNING, runningBudget);
            if (morningEvent != null) {
                allAvailableEvents.remove(morningEvent);
                runningBudget -= (int) morningEvent.getCost();
                tempDay.setMorningEvent(morningEvent);
            } else {
                // TODO ADD RECOMMENDED EVENT
                tempDay.setMorningEvent(emptyEvent);
            }

            // Picks afternoon event and updates budget
            Event afternoonEvent = getEvent(KEY_AFTERNOON, runningBudget);
            if (afternoonEvent != null) {
                allAvailableEvents.remove(afternoonEvent);
                runningBudget -= (int) afternoonEvent.getCost();
                tempDay.setAfternoonEvent(afternoonEvent);
            } else {
                // TODO ADD RECOMMENDED EVENT
                tempDay.setAfternoonEvent(emptyEvent);
            }

            // Picks evening event and updates budget
            Event eveningEvent = getEvent(KEY_EVENING, runningBudget);
            if (eveningEvent != null) {
                allAvailableEvents.remove(eveningEvent);
                runningBudget -= (int) eveningEvent.getCost();
                tempDay.setEveningEvent(eveningEvent);
            } else {
                // TODO ADD RECOMMENDED EVENT
                tempDay.setEveningEvent(emptyEvent);
            }

            dayPlans.add(tempDay);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static Date addToDate(LocalDate d1, int numDays) {
        return java.sql.Date.valueOf((DAYS.addTo(d1, numDays)).toString());
    }


    private Event getEvent(String timeOfDay, int runningBudget) {

        Event randomEvent = null;
        ArrayList<Event> alreadyChecked = new ArrayList<>();

        while (randomEvent == null) {
            randomEvent = getRandomElement(allAvailableEvents);

            if (alreadyChecked.size() == allAvailableEvents.size()) {
                // Returns null if we have searched all available event
                return null;
            }

            if (!alreadyChecked.contains(randomEvent)) {
                // True if we have not already selected this event
                alreadyChecked.add(randomEvent);
                if (eventIsAvailable(randomEvent, timeOfDay)) {
                    // True if the event fits the time slot
                    if (eventIsWithinBudget(randomEvent, runningBudget)) {
                        // Returns the event if it's within our budget
                        return randomEvent;
                    }
                }
            }
            // Function reiterates the search
            randomEvent = null;
        }
        return randomEvent;
    }

    // Returns a random event from a list of events
    public Event getRandomElement(List<Event> list) {
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    public Boolean eventIsAvailable(Event event, String timeOfDay) {
        if (timeOfDay.contentEquals(KEY_MORNING)) {
            return event.isAvailableMorning();
        } else if (timeOfDay.contentEquals(KEY_AFTERNOON)) {
            return event.isAvailableAfternoon();
        } else {
            return event.isAvailableEvening();
        }
    }

    // Checks if the cost of an event is within the budget
    public Boolean eventIsWithinBudget(Event event, int budget) {
        int eventCost = (int) event.getCost();
        if (eventCost > budget) {
            return false;
        } else {
            return true;
        }
    }

    // Sends information to review fragment
    private void sendBundle(int budget) {
        Fragment fragment = new TripReviewFragment();

        Bundle bundle = new Bundle();
        bundle.putString("trip_name", etTripName.getText().toString());
        bundle.putString("start_date", etStartDate.getText().toString());
        bundle.putString("end_date", etEndDate.getText().toString());
        bundle.putInt("number_days", numDays);
        bundle.putInt("budget", budget);
        bundle.putParcelableArrayList("selected_tags", selectedTags);
        bundle.putParcelable("city", city);
        bundle.putParcelableArrayList("dayPlans", dayPlans);
        fragment.setArguments(bundle);

        MainActivity.fragmentManager.beginTransaction()
                .replace(R.id.flContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}
