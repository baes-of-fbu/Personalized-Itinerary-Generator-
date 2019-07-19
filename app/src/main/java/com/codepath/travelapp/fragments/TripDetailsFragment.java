package com.codepath.travelapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.travelapp.Models.Trip;
import com.codepath.travelapp.R;
import com.parse.ParseFile;

public class TripDetailsFragment extends Fragment {

    private final String TAG = "TripDetailsFragment";

    private ImageView ivCoverPhoto;
    private TextView tvTripName;
    private ImageView ivProfileImage;
    private TextView tvUsername;
    private TextView tvTravelDates;
    private TextView tvDays;
    private TextView tvBudget;
    private RecyclerView rvSchedule;
    private Trip trip;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trip_details,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivCoverPhoto = view.findViewById(R.id.ivCoverPhoto);
        tvTripName = view.findViewById(R.id.tvTripName);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvTravelDates = view.findViewById(R.id.tvTravelDates);
        tvDays = view.findViewById(R.id.tvDays);
        tvBudget = view.findViewById(R.id.tvBudget);
        rvSchedule = view.findViewById(R.id.rvSchedule);

        Bundle bundle = getArguments();
        trip = (Trip) bundle.getSerializable("Trip");

        if (trip != null) {
            tvTripName.setText(trip.getName());
            tvUsername.setText(trip.getOwner().getUsername());
            tvTravelDates.setText(trip.getStartDate() + " - " + trip.getEndDate());
            tvDays.setText(trip.getNumDays().toString());
            tvBudget.setText("$" + trip.getBudget());

            Glide.with(getContext())
                    .load(trip.getImage().getUrl())
                    .into(ivCoverPhoto);

            ParseFile image = (ParseFile) trip.getOwner().get("profileImage");
            Glide.with(getContext())
                    .load(image.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfileImage);
        }
    }
}
