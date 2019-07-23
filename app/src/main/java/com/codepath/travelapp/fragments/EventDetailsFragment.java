package com.codepath.travelapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.codepath.travelapp.Models.Event;
import com.codepath.travelapp.R;

public class EventDetailsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_details,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvEventName = view.findViewById(R.id.tvEveningName);
        ImageView ivCoverPhoto = view.findViewById(R.id.ivCoverPhoto);
        TextView tvCost = view.findViewById(R.id.tvCost);
        RatingBar rbRating = view.findViewById(R.id.rbRating);
        TextView tvAddress = view.findViewById(R.id.tvAddress);
        TextView tvDescription = view.findViewById(R.id.tvDescription);

        Bundle bundle = getArguments();
        assert bundle != null;
        Event event = (Event) bundle.getSerializable("Event");

        if (event != null) {
            tvEventName.setText(event.getName());
            tvCost.setText(event.getCost().toString());
            rbRating.setRating((float) event.getRating());
            tvAddress.setText(event.getAddress());
            tvDescription.setText(event.getDescription());

            // Glide for coverPhoto
        }
    }
}
