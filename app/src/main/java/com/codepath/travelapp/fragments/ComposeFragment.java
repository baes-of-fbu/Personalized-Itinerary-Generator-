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
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static java.time.temporal.ChronoUnit.DAYS;

public class ComposeFragment extends Fragment {

    private EditText etTripName;
    private EditText etStartDate;
    private EditText etEndDate;
    private EditText etBudget;
    private Spinner spCity;

    private String tripName;
    private String startDate;
    private String endDate;
    private int numDays;
    private String budgetString;
    private String cityName;
    private City city;

    private TagGridAdapter adapter;
    private List<Tag> tags;

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
        Button btnGenerate = view.findViewById(R.id.btnGenerate);

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
                    } else if (cityName.contains("Select city")) {
                        Toast.makeText(getContext(), "Select city", Toast.LENGTH_LONG).show();
                    } else {
                        queryForCity(cityName);
                    }
                }
            }
        });

        final RecyclerView rvTags = view.findViewById(R.id.rvTags);

        // Initialize tags
        ParseQuery<Tag> tagQuery = new ParseQuery<>(Tag.class);
        tagQuery.setLimit(10);
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
                    gridLayoutManager.setSpanCount(3);

                    // Set layout manager to position the items
                    rvTags.setLayoutManager(gridLayoutManager);
                } else {
                    e.printStackTrace();
                    Log.d("DEBUG", "Fetch timeline error: " + e.toString());
                }
            }
        });
    }

    // Returns the parse city object associated with the string parameter
    private void queryForCity(final String cityName) {
        ParseQuery<City> cityQuery = new ParseQuery<>(City.class);
        cityQuery.whereEqualTo(City.KEY_NAME, cityName);
        cityQuery.findInBackground(new FindCallback<City>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void done(List<City> objects, ParseException e) {
                if (e == null) {
                    city = objects.get(0);
                    // Assumes only one city is associated with the name
                    Toast.makeText(getContext(), objects.get(0).toString(), Toast.LENGTH_LONG).show();
                    sendToReviewFragment();
                } else {
                    e.printStackTrace();
                    Log.d("ComposeFragment", "Failed to query city: " + e.toString());
                    Toast.makeText(getContext(), "Failed to query city: " + cityName, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Sends bundle to Review Fragment
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendToReviewFragment() {
        ArrayList<Tag> tags = adapter.getSelectedTags();
        Fragment fragment = new TripReviewFragment();

        int budget = Integer.parseInt(etBudget.getText().toString());
        ArrayList<DayPlan> dayPlans = generateSchedule(city, numDays, budget, tags);

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

    // Selecting a Date and displaying it in EditText
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static long getDifferenceDays(LocalDate d1, LocalDate d2) {
        long diff = DAYS.between(d1, d2);
        return diff + 1;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<DayPlan> generateSchedule(City city, int numDays, int budget, List<Tag> tags) {

        ArrayList<DayPlan> dayPlans = new ArrayList<>();

        // Get available events based off tags
        List<Event> allAvailableEvents = getAvailableEvents(city, tags);
        List<Event> morningEvents = filterEventsByTime(city, tags, Event.KEY_MORNING);
        List<Event> afternoonEvents = filterEventsByTime(city, tags, Event.KEY_AFTERNOON);
        List<Event> eveningEvents = filterEventsByTime(city, tags, Event.KEY_EVENING);

        int runningBudget = budget; // Stores budget for the entire trip

        // Loop through each day
        for (int day = 0; day < numDays; day++) {

            DayPlan tempDay = new DayPlan();

            // Picks morning event and updates budget
            Event morningEvent = getEvent(allAvailableEvents, morningEvents, runningBudget);
            if (morningEvent != null) {
                runningBudget -= (int) morningEvent.getCost();
            }
            tempDay.setMorningEvent(morningEvent);

            // Picks afternoon event and updates budget
            Event afternoonEvent = getEvent(allAvailableEvents, afternoonEvents, runningBudget);
            if (afternoonEvent != null) {
                runningBudget -= (int) afternoonEvent.getCost();
            }
            tempDay.setAfternoonEvent(afternoonEvent);

            // Picks evening event and updates budget
            Event eveningEvent = getEvent(allAvailableEvents, eveningEvents, runningBudget);
            if (eveningEvent != null) {
                runningBudget -= (int) eveningEvent.getCost();
            }
            tempDay.setEveningEvent(eveningEvent);

            dayPlans.add(tempDay);
        }

        // Returns list of days
        return dayPlans;
    }

    // Returns a list of available events via queries
    private List<Event> getAvailableEvents(City city, List<Tag> tags) {
        return null;
    }

    private List<Event> filterEventsByTime(City city, List<Tag> tags, String timeSlot) {
        return null;
    }

    // Returns an event that has not been used in the schedule yet,
    // or null if no event was selected
    private Event getEvent(List<Event> allAvailableEvents, List<Event> filteredEvents, int budget) {

        Event randomEvent = null;
        // Searches for an event until event is found or there are no more to choose from
        while (randomEvent == null) {

            // Breaks loop if there are no events to pick from
            if (filteredEvents.size() == 0) {
                Toast.makeText(getContext(), "Could not find an event", Toast.LENGTH_LONG).show();

                // TODO account for case where no event was found
                randomEvent = null; // Returns empty event
                break;
            }

            // Random event is picked from the filtered list of events
            randomEvent = getRandomElement(filteredEvents);

            // If the event is not used and is within the budget, it is removed from the available
            // events list to prevent it from being selected again in other days
            if (allAvailableEvents.contains(randomEvent) && isWithinBudget(randomEvent, budget)) {
                allAvailableEvents.remove(randomEvent);
                Toast.makeText(getContext(), "Found an event!", Toast.LENGTH_LONG).show();
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

    // Checks if the cost of an event is within the budget
    public Boolean isWithinBudget(Event event, int budget) {
        int eventCost = (int) event.getCost();
        if (eventCost > budget) {
            return false;
        } else {
            return true;
        }
    }
}
