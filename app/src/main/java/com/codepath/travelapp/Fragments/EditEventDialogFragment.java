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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.codepath.travelapp.Models.DayPlan;
import com.codepath.travelapp.R;

public class EditEventDialogFragment extends DialogFragment {

    private TextView tvEditEvent;
    private TextView tvCancelEvent;
    private TextView tvDeleteEvent;
    private String actionToReturn;
    private String timeOfDay;
    private DayPlan currDayPlan;

    public EditEventDialogFragment() {
        // Empty constructor is required for DialogFragment
    }

    // TODO REMOVE PARAMETERS
    public static EditEventDialogFragment newInstance(String timeOfDay, DayPlan currDayPlan) {
        EditEventDialogFragment fragment = new EditEventDialogFragment();
        Bundle data = new Bundle();
        data.putString("timeOfDay", timeOfDay);
        data.putParcelable("currDayPlan", currDayPlan);
        fragment.setArguments(data);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        timeOfDay = bundle.getString("timeOfDay");
        currDayPlan = bundle.getParcelable("currDayPlan");
        return inflater.inflate(R.layout.fragment_edit_event_dialog, container, false);
    }

    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.6), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvEditEvent = view.findViewById(R.id.tvEditEvent);
        tvCancelEvent = view.findViewById(R.id.tvCancelEvent);
        tvDeleteEvent = view.findViewById(R.id.tvDeleteEvent);
        actionToReturn = "";
        addOnClickListeners();
    }

    private Listener mListener;

    public void setListener(Listener listener) {
        mListener = listener;

    }

    public interface Listener {
        void returnData(String result, String timeOfDay, DayPlan currDayPlan);
    }

    private void addOnClickListeners() {
        tvEditEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionToReturn = getString(R.string.edit);
                mListener.returnData(actionToReturn, timeOfDay, currDayPlan);
                dismiss();
            }
        });

        tvCancelEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.returnData(actionToReturn, timeOfDay, currDayPlan);
                dismiss();
            }
        });

        tvDeleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionToReturn = getString(R.string.delete);
                mListener.returnData(actionToReturn, timeOfDay, currDayPlan);
                dismiss();
            }
        });
    }
}
