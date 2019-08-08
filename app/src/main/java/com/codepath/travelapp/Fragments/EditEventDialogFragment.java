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

import com.codepath.travelapp.R;

import java.util.Objects;

public class EditEventDialogFragment extends DialogFragment {

    private TextView tvRegenerateEvent;
    private TextView tvCancelEdit;
    private TextView tvRemoveEvent;

    public EditEventDialogFragment() {}

    public static EditEventDialogFragment newInstance() {
        return new EditEventDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_event_dialog, container, false);
    }

    public void onResume() {
        // Store access variables for window and blank point
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        Point size = new Point();
        if (window != null) {
            Display display = window.getWindowManager().getDefaultDisplay();
            display.getSize(size);
            // Set the width of the dialog proportional to 90% of the screen width
            window.setLayout((int) (size.x * 0.9), WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            super.onResume();
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvRegenerateEvent = view.findViewById(R.id.tvRegenerateEvent);
        tvCancelEdit = view.findViewById(R.id.tvCancelEdit);
        tvRemoveEvent = view.findViewById(R.id.tvRemoveEvent);
        addOnClickListeners();
    }

    private Listener mListener;

    public void setListener(Listener listener) {
        mListener = listener;

    }

    public interface Listener {
        void returnData(String result);
    }

    private void addOnClickListeners() {
        tvRegenerateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.returnData(getString(R.string.regenerate_event));
                dismiss();
            }
        });

        tvCancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.returnData(getString(R.string.cancel));
                dismiss();
            }
        });

        tvRemoveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.returnData(getString(R.string.remove_event));
                dismiss();
            }
        });
    }
}
