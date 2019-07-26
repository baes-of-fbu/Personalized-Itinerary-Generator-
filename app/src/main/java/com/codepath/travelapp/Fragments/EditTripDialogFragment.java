package com.codepath.travelapp.Fragments;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.codepath.travelapp.Adapters.EditableEventAdapter;
import com.codepath.travelapp.Models.DayPlan;
import com.codepath.travelapp.Models.Event;
import com.codepath.travelapp.R;

import java.util.ArrayList;

public class EditTripDialogFragment extends DialogFragment {

    private EditText etEditTripname;
    private Button btnSave;
    private Button btnCancel;
    private ListView lvEditSchedule;

    public EditTripDialogFragment() {
        // Empty constructor is required for DialogFragment
    }

    public static EditTripDialogFragment newInstance(ArrayList<DayPlan> dayPlans, String tripName) {

        Bundle args = new Bundle();
        args.putParcelableArrayList("dayPlans", dayPlans);
        args.putString("tripName", tripName);
        EditTripDialogFragment fragment = new EditTripDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_trip, container, false);
    }

    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 1), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etEditTripname = view.findViewById(R.id.etEditTripName);
        btnSave = view.findViewById(R.id.btnEditSave);
        btnCancel = view.findViewById(R.id.btnEditCancel);
        lvEditSchedule = view.findViewById(R.id.lvEditSchedule);

        Bundle bundle = getArguments();
        String tripName = bundle.getString("tripName");
        ArrayList<DayPlan> dayPlans = bundle.getParcelableArrayList("dayPlans");
        ArrayList<Event> events = getEventsFromDayPlans(dayPlans);
        EditableEventAdapter adapter = new EditableEventAdapter(getContext(), events);
        lvEditSchedule.setAdapter(adapter);
        etEditTripname.setText(tripName);
        getDialog().setTitle("Testing");
        addOnClickListeners();
    }

    private void addOnClickListeners() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss(); // Closes the fragment
            }
        });
    }

    private ArrayList<Event> getEventsFromDayPlans(ArrayList<DayPlan> dayPlans) {
        ArrayList<Event> events = new ArrayList<>();
        for (int i = 0; i < dayPlans.size(); i ++) {
            DayPlan currPlan = dayPlans.get(i);
            Event morningEvent = currPlan.getMorningEvent();
            if (morningEvent != null) {
                events.add(morningEvent);
            } else {
                // TODO handle null case
            }
            Event afternoonEvent = currPlan.getAfternoonEvent();
            if (afternoonEvent != null) {
                events.add(afternoonEvent);
            } else {
                // TODO handle null case
            }
            Event eveningEvent = currPlan.getEveningEvent();
            if (eveningEvent != null) {
                events.add(eveningEvent);
            } else {
                // TODO handle null case
            }
        }
        return events;
    }

}
