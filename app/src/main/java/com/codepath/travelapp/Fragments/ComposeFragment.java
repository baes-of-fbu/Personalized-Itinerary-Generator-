package com.codepath.travelapp.Fragments;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
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
import java.time.format.DateTimeFormatter;
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
    private Toolbar tbCompose;

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
        tbCompose = view.findViewById(R.id.tbCompose);
        etTripName = view.findViewById(R.id.etTripName);
        etStartDate = view.findViewById(R.id.etStartDate);
        etEndDate = view.findViewById(R.id.etEndDate);
        etBudget = view.findViewById(R.id.etBudget);
        spCity = view.findViewById(R.id.spCity);
        btnGenerate = view.findViewById(R.id.btnGenerate);

        tbCompose.setBackgroundColor(getContext().getResources().getColor(R.color.LightSkyBlue));
        tbCompose.setTitle(getContext().getString(R.string.compose_trip));
        tbCompose.setTitleTextColor(getContext().getResources().getColor(R.color.white));

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
                    showAlertDialog("Unable to display tags.");
                }
            }
        });
    }

    // Adds onClickListeners for etStartDate, etEndDate, and btnGenerate
    private void addOnClickListeners() {

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
                    showAlertDialog("Specify trip name");
                } else if (startDate.length() == 0) {
                    showAlertDialog("Specify start date");
                } else if (endDate.length() == 0) {
                    showAlertDialog("Specify end date");
                } else {
                    numDays = (int) getDifferenceBetweenDays(TripReviewFragment
                            .getParseDate(startDate), TripReviewFragment.getParseDate(endDate));
                    if (numDays < 1) {
                        showAlertDialog("Invalid dates. Please fix your start and/or end date");
                    } else if (dateHasPassed(TripReviewFragment.getParseDate(startDate))) {
                        showAlertDialog("Start date has already passed");
                    } else if (etBudget.getText().toString().length() == 0) {
                        showAlertDialog("Specify budget");
                    } else {
                        budget = Integer.parseInt(etBudget.getText().toString());
                        if (budget <= 0) {
                            showAlertDialog("Minimum budget is $0.");
                        } else if (cityName.contains("Select city")) {
                            showAlertDialog("Select city");
                        } else {
                            selectedTags = adapter.getSelectedTags();
                            generateSchedule(cityName);
                        }
                    }
                }
            }
        });

        etTripName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void afterTextChanged(Editable editable) {
                if (userInputComplete()) {
                    btnGenerate.setBackground(getContext().getDrawable(R.drawable.rounded_btn));
                }
            }
        });

        etStartDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void afterTextChanged(Editable editable) {
                if (userInputComplete()) {
                    btnGenerate.setBackground(getContext().getDrawable(R.drawable.rounded_btn));
                }
            }
        });

        etEndDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void afterTextChanged(Editable editable) {
                if (userInputComplete()) {
                    btnGenerate.setBackground(getContext().getDrawable(R.drawable.rounded_btn));
                }
            }
        });

        etBudget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void afterTextChanged(Editable editable) {
                if (userInputComplete()) {
                    btnGenerate.setBackground(getContext().getDrawable(R.drawable.rounded_btn));
                }
            }
        });
        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (userInputComplete()) {
                    btnGenerate.setBackground(getContext().getDrawable(R.drawable.rounded_btn));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Boolean userInputComplete() {
        // Checks if all the inputs are correct
        if (etTripName.getText().toString().length() > 0
        && etStartDate.getText().toString().length() > 0
        && etEndDate.getText().toString().length() > 0
        && etBudget.getText().toString().length() > 0) {
            numDays = (int) getDifferenceBetweenDays(TripReviewFragment.getParseDate(etStartDate.getText().toString()),
                    TripReviewFragment.getParseDate(etEndDate.getText().toString()));
            if (numDays >= 1) {
                if (!dateHasPassed(TripReviewFragment.getParseDate(etStartDate.getText().toString()))) {
                    if (Integer.parseInt(etBudget.getText().toString()) >= 0) {
                        return (!spCity.getSelectedItem().toString().contains("Select city"));
                    }
                }
            }
        }
        return false;
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
                                showAlertDialog("Error with compose.");
                            }
                        }
                    });
                } else {
                    e.printStackTrace();
                    showAlertDialog("Error with compose.");
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.N)
    static LocalDate getParseDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        return LocalDate.parse(date, formatter);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private static Date convertToDate(LocalDate d1) {
        return java.sql.Date.valueOf(d1.toString());
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

            // Converts date to include day of the week
            String tempDate = date.toString();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-dd");
            Date newDate = null;
            try {
                newDate = simpleDateFormat.parse(tempDate);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

            tempDay.setDate(newDate);

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

    private void showAlertDialog(String message) {
        AlertDialog dialog = new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                .setTitle(message)
                .setPositiveButton("OK", null)
                .create();
        dialog.show();
    }
}
