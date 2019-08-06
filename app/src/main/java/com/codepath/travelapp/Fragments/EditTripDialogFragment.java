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

public class EditTripDialogFragment extends DialogFragment {

    private TextView tvEdit;
    private TextView tvCancel;
    private TextView tvDelete;
    private String actionToReturn;

    public EditTripDialogFragment() {
    }

    public static EditTripDialogFragment newInstance() {
        return new EditTripDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_editdialog_options, container, false);
    }

    public void onResume() {
        // Store access variables for window and blank point
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        Point size = new Point();
        if (window != null) {
            Display display = window.getWindowManager().getDefaultDisplay();
            display.getSize(size);
            // Set the width of the dialog proportional to 75% of the screen width
            window.setLayout((int) (size.x * 0.6), WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            super.onResume();
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvEdit = view.findViewById(R.id.tvEdit);
        tvCancel = view.findViewById(R.id.tvCancel);
        tvDelete = view.findViewById(R.id.tvDelete);
        actionToReturn = "";

        addOnClickListeners();
    }

    private void addOnClickListeners() {
        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionToReturn = getString(R.string.edit);
                sendBackResult();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionToReturn = getString(R.string.delete);
                sendBackResult();
            }
        });
    }

    // Used to return info to the parent
    public interface EditTripDialogListener {
        void onFinishEditDialog(String inputText);
    }

    // Call this method to send the data back to the parent fragment
    private void sendBackResult() {
        // 'getTargetFragment' will be set when the dialog is displayed
        EditTripDialogListener listener = (EditTripDialogListener) getTargetFragment();
        if (listener != null) {
            listener.onFinishEditDialog(actionToReturn);
            dismiss();
        }
    }
}
