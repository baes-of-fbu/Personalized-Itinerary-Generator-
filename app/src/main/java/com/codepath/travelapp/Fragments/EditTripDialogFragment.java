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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.codepath.travelapp.R;

public class EditTripDialogFragment extends DialogFragment {

    private EditText etEditTripname;
    private Button btnSave;
    private Button btnCancel;

    public EditTripDialogFragment() {
        // Empty constructor is required for DialogFragment
    }

    public static EditTripDialogFragment newInstance() {

        Bundle args = new Bundle();

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
        window.setLayout((int) (size.x * 0.85), WindowManager.LayoutParams.WRAP_CONTENT);
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


        etEditTripname.setText("hello");
        getDialog().setTitle("Testing");

        addOnClickListeners();
    }

    public void addOnClickListeners() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss(); // Closes the fragment
            }
        });
    }
}
