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

import com.bumptech.glide.Glide;
import com.codepath.travelapp.Models.Event;
import com.codepath.travelapp.R;

import java.util.Objects;

public class EventDetailsFragment extends Fragment {

    private TextView tvEventName;
    private ImageView ivCoverPhoto;
    private TextView tvCost;
    private RatingBar rbRating;
    private TextView tvAddress;
    private TextView tvDescription;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_details,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvEventName = view.findViewById(R.id.tvEventName);
        ivCoverPhoto = view.findViewById(R.id.ivCoverPhoto);
        tvCost = view.findViewById(R.id.tvCost);
        rbRating = view.findViewById(R.id.rbRating);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvDescription = view.findViewById(R.id.tvDescription);

        Bundle bundle = getArguments();
        assert bundle != null;
        Event event = (Event) bundle.getParcelable("event");

        if (event != null) {
            tvEventName.setText(event.getName());
            tvCost.setText(event.getCost().toString());
            rbRating.setRating(event.getRating().floatValue());
            tvAddress.setText(event.getAddress());
            tvDescription.setText(event.getDescription());

            Glide.with(Objects.requireNonNull(getContext()))
                    .load(event.getImage().getUrl())
                    .into(ivCoverPhoto);

            addOnClickListners();
        }
    }

    private void addOnClickListners() {
        tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
