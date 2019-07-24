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

    private TagGridAdapter adapter;
    private List<Tag> tags;
    private ArrayList<Event> allAvailableEvents;
    private ArrayList<Event> morningEvents = new ArrayList<>();
    private ArrayList<Event> afternoonEvents = new ArrayList<>();
    private ArrayList<Event> eveningEvents = new ArrayList<>();
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
                    tags = objects;
                    // Create adapter passing in the sample user data
                    adapter = new TagGridAdapter(tags);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static Date addToDate(LocalDate d1, int numDays) {
        return java.sql.Date.valueOf((DAYS.addTo(d1, numDays)).toString());
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
                    parseAllAvailableEvents(budget);
                } else {
                    Log.d("ComposeFragment", "Failed to query city: " + e.toString());
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Failed to query city: " + cityName, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Sends network requests for all events in a city, based off the tags
    private void parseAllAvailableEvents(final int budget) {

        allAvailableEvents = new ArrayList<>();
        List<ParseQuery<Event>> queries = new ArrayList<ParseQuery<Event>>();

        // Create queries for each tag
        for (int i = 0; i < tags.size(); i++) {
            Tag tag = tags.get(i);
            ParseQuery<Event> eventQuery = tag.getEventsRelation().getQuery();
            eventQuery.whereEqualTo("city", city);
            queries.add(eventQuery);
        }

        // Send all queries in one main query
        ParseQuery<Event> mainQuery = ParseQuery.or(queries);
        mainQuery.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> objects, ParseException e) {
                if (e == null) {
                    Log.d("ComposeFragment", "All events" + objects.toString());
                    Toast.makeText(getContext(), "Query for available events successful", Toast.LENGTH_LONG).show();
                    allAvailableEvents.addAll(objects);
                    parseEventsByTime(Event.KEY_MORNING, budget);
                } else {
                    Log.d("Compose Fragment", e.toString());
                    Toast.makeText(getContext(), "Query for available events unsuccessful", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Sends network requests for all events in a city, based off the tags and time of day
    private void parseEventsByTime(String timeSlot, final int budget) {

        List<ParseQuery<Event>> queries = new ArrayList<ParseQuery<Event>>();
        // Create queries for each tag
        if (timeSlot == Event.KEY_MORNING) {
            for (int i = 0; i < tags.size(); i++) {
                Tag tag = tags.get(i);
                ParseQuery<Event> eventQuery = tag.getEventsRelation().getQuery();
                eventQuery.whereEqualTo("city", city);
                eventQuery.whereEqualTo("morning", true);
                queries.add(eventQuery);
            }

            // Send all queries in one main query
            ParseQuery<Event> mainQuery = ParseQuery.or(queries);
            mainQuery.findInBackground(new FindCallback<Event>() {
                @Override
                public void done(List<Event> objects, ParseException e) {
                    if (e == null) {
                        Log.d("ComposeFragment", "Morning events: " + objects.toString());
                        Toast.makeText(getContext(), "Morning query successful", Toast.LENGTH_LONG).show();
                        morningEvents.addAll(objects);
                        parseEventsByTime(Event.KEY_AFTERNOON, budget);
                    } else {
                        Log.d("Compose Fragment", e.toString());
                        Toast.makeText(getContext(), "Morning query unsuccessful", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else if (timeSlot == Event.KEY_AFTERNOON) {
            for (int i = 0; i < tags.size(); i++) {
                Tag tag = tags.get(i);
                ParseQuery<Event> eventQuery = tag.getEventsRelation().getQuery();
                eventQuery.whereEqualTo("city", city);
                eventQuery.whereEqualTo("afternoon", true);
                queries.add(eventQuery);
            }

            // Send all queries in one main query
            ParseQuery<Event> mainQuery = ParseQuery.or(queries);
            mainQuery.findInBackground(new FindCallback<Event>() {
                @Override
                public void done(List<Event> objects, ParseException e) {
                    if (e == null) {
                        Log.d("ComposeFragment", "Afternoon events: " + objects.toString());
                        Toast.makeText(getContext(), "Afternoon query successful", Toast.LENGTH_LONG).show();
                        afternoonEvents.addAll(objects);
                        parseEventsByTime(Event.KEY_EVENING, budget);
                    } else {
                        Log.d("Compose Fragment", e.toString());
                        Toast.makeText(getContext(), "Afternoon query unsuccessful", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else if (timeSlot == Event.KEY_EVENING) {
            for (int i = 0; i < tags.size(); i++) {
                Tag tag = tags.get(i);
                ParseQuery<Event> eventQuery = tag.getEventsRelation().getQuery();
                eventQuery.whereEqualTo("city", city);
                eventQuery.whereEqualTo("evening", true);
                queries.add(eventQuery);
            }

            // Send all queries in one main query
            ParseQuery<Event> mainQuery = ParseQuery.or(queries);
            mainQuery.findInBackground(new FindCallback<Event>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void done(List<Event> objects, ParseException e) {
                    if (e == null) {
                        Log.d("ComposeFragment", "Evening events: " + objects.toString());
                        Toast.makeText(getContext(), "Evening query successful", Toast.LENGTH_LONG).show();
                        eveningEvents.addAll(objects);
                        createDayPlans(allAvailableEvents, morningEvents, afternoonEvents, eveningEvents, budget);
                        sendBundle(budget);
                    } else {
                        Log.d("Compose Fragment", e.toString());
                        Toast.makeText(getContext(), "Evening query unsuccessful", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    // Adds events to day plans
    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void createDayPlans(List<Event> allAvailableEvents, List<Event> morningEvents,
                                List<Event> afternoonEvents, List<Event> eveningEvents,
                                int runningBudget) {
        dayPlans = new ArrayList<>();

        // Loop through each day
        for (int day = 0; day < numDays; day++) {

            DayPlan tempDay = new DayPlan();

            Date date = addToDate(TripReviewFragment.getParseDate(startDate), numDays);
            tempDay.setDate(date);

            // Picks morning event and updates budget
            Event morningEvent = getEvent(allAvailableEvents, morningEvents, runningBudget);
            if (morningEvent != null) {
                runningBudget -= (int) morningEvent.getCost();
                tempDay.setMorningEvent(morningEvent);
            }


            // Picks afternoon event and updates budget
            Event afternoonEvent = getEvent(allAvailableEvents, afternoonEvents, runningBudget);
            if (afternoonEvent != null) {
                runningBudget -= (int) afternoonEvent.getCost();
                tempDay.setAfternoonEvent(afternoonEvent);
            }


            // Picks evening event and updates budget
            Event eveningEvent = getEvent(allAvailableEvents, eveningEvents, runningBudget);
            if (eveningEvent != null) {
                runningBudget -= (int) eveningEvent.getCost();
                tempDay.setEveningEvent(eveningEvent);
            }

            dayPlans.add(tempDay);
        }
    }


    // Returns an event that has not been used in the schedule yet, or null if no event was selected
    private Event getEvent(List<Event> allAvailableEvents, List<Event> filteredEvents, int budget) {

        Event randomEvent = null;
        // Searches for an event until event is found or there are no more to choose from
        while (randomEvent == null) {

            // Breaks loop if there are no events to pick from
            if (filteredEvents.size() == 0) {
                Log.d("ComposeFragment", "Could not find an event");
                Toast.makeText(getContext(), "Could not find an event", Toast.LENGTH_LONG).show();

                // TODO account for case where no event was found
                randomEvent = null; // Returns empty event
                break;
            }

            // Random event is picked from the filtered list of events
            randomEvent = getRandomElement(filteredEvents);

            // If the event is not used and is within the budget, it is removed from the available
            // events list to prevent it from being selected again in other days
            if (isWithinBudget(randomEvent, budget)) {
                Event eventSelected = isAvailable(allAvailableEvents, randomEvent);
                if (eventSelected != null) {
                    allAvailableEvents.remove(eventSelected);
                    Log.d("ComposeFragment", "Event selected: " + eventSelected.getName());
                    Toast.makeText(getContext(), "Found an event!", Toast.LENGTH_LONG).show();
                }
                break;
            } else {
                filteredEvents.remove(randomEvent);
                randomEvent = null; // Repeats search is no event found
            }
        }
        return randomEvent;
    }

    // Returns a random event from a list of events
    public Event getRandomElement(List<Event> list) {
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    public Event isAvailable(List<Event> availableEvents, Event event) {
        String eventName = event.getName();

        for (int i = 0; i < availableEvents.size(); i++) {
            Event currentEvent = availableEvents.get(i);
            if (eventName.contentEquals(currentEvent.getName())) {
                return currentEvent;
            }
        }
        return null;
    }

    // Checks if the cost of an event is within the budget
    public Boolean isWithinBudget(Event event, int budget) {
        int eventCost = (int) event.getCost();
        if (eventCost > budget) {
            return false;
        } else {
            return true;
        }
    }

    // Sends information to review fragment
    private void sendBundle(int budget) {
        ArrayList<Tag> tags = adapter.getSelectedTags();
        Fragment fragment = new TripReviewFragment();

        Bundle bundle = new Bundle();
        bundle.putString("trip_name", etTripName.getText().toString());
        bundle.putString("start_date", etStartDate.getText().toString());
        bundle.putString("end_date", etEndDate.getText().toString());
        bundle.putInt("number_days", numDays);
        bundle.putInt("budget", budget);
        bundle.putParcelableArrayList("selected_tags", tags);
        bundle.putParcelable("city", city);
        bundle.putParcelableArrayList("dayPlans", dayPlans);
        fragment.setArguments(bundle);

        MainActivity.fragmentManager.beginTransaction()
                .replace(R.id.flContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}
