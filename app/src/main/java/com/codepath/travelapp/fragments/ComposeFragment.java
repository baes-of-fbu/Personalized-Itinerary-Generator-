package com.codepath.travelapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travelapp.Adapters.TagGridAdapter;
import com.codepath.travelapp.MainActivity;
import com.codepath.travelapp.Models.Tag;
import com.codepath.travelapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

public class ComposeFragment extends Fragment {

    private final String TAG = "ComposeFragment";

    private EditText etTripName;
    private EditText etStartDate;
    private EditText etEndDate;
    private EditText etBudget;
    private EditText etCity;
    private Button btnGenerate;

    List<Tag> tags;

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

        RecyclerView rvTags = (RecyclerView) view.findViewById(R.id.rvTags);

        // Initialize contacts
        ParseQuery<Tag> tagQuery = new ParseQuery<Tag>(Tag.class);
        tagQuery.setLimit(20);
        tagQuery.findInBackground(new FindCallback<Tag>() {
            @Override
            public void done(List<Tag> objects, ParseException e) {
                if (e == null) {
                    Log.d("DEBUG", "Successful query for tags");
                    tags = objects;
                } else {
                    e.printStackTrace();
                    Log.d("DEBUG", "Fetch timeline error: " + e.toString());
                }
            }
        });

        // Create adapter passing in the sample user data
        TagGridAdapter adapter = new TagGridAdapter(tags);
        // Attach the adapter to the recyclerview to populate items
        rvTags.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), GridLayoutManager.VERTICAL);
        gridLayoutManager.setSpanCount(3);
        // Set layout manager to position the items
        rvTags.setLayoutManager(gridLayoutManager);
    }
}
