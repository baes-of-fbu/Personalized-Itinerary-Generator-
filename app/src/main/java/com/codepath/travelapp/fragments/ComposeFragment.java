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
    private String budget;
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
                budget = etBudget.getText().toString();
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
                    } else if (budget.length() == 0) {
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

        // Initialize contacts
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
                    // Assumes only one object associated with the name passed in
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

        Bundle bundle = new Bundle();
        bundle.putString("trip_name", etTripName.getText().toString());
        bundle.putString("start_date", etStartDate.getText().toString());
        bundle.putString("end_date", etEndDate.getText().toString());
        bundle.putInt("number_days", numDays);
        bundle.putString("budget", etBudget.getText().toString());
        bundle.putParcelableArrayList("selected_tags", tags);
        bundle.putParcelable("city", city);
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
}
