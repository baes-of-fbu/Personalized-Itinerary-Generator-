package com.codepath.travelapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.codepath.travelapp.MainActivity;
import com.codepath.travelapp.R;
import com.parse.ParseFile;

public class ComposeFragment extends Fragment {

    private final String TAG = "ComposeFragment";

    private EditText etTripName;
    private EditText etStartDate;
    private EditText etEndDate;
    private EditText etBudget;
    private EditText etCity;
    private Button btnGenerate;

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
        etCity = view.findViewById(R.id.etCity);
        btnGenerate = view.findViewById(R.id.btnGenerate);

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new TripReviewFragment();

                Bundle bundle = new Bundle();
                bundle.putString("trip_name", etTripName.getText().toString());
                bundle.putString("start_date", etStartDate.getText().toString());
                bundle.putString("end_date", etEndDate.getText().toString());
                bundle.putString("budget", etBudget.getText().toString());
                bundle.putString("city", etCity.getText().toString());
                fragment.setArguments(bundle);

                MainActivity.fragmentManager.beginTransaction()
                        .replace(R.id.flContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}
