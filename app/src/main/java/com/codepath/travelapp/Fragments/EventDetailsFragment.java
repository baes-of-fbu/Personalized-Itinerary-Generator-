package com.codepath.travelapp.Fragments;

import android.content.Intent;
import android.net.Uri;
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
    private String location;
    private Event event;

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
        event = (Event) bundle.getParcelable("event");

        if (event != null) {
            tvEventName.setText(event.getName());
            tvCost.setText(event.getCost().toString());
            rbRating.setRating(event.getRating().floatValue());
            // TODO add SpannableString for address to do .setSpan(new UnderlineSpan(), , , ); then set text
            tvAddress.setText(event.getAddress());
            tvDescription.setText(event.getDescription());

            Glide.with(Objects.requireNonNull(getContext()))
                    .load(event.getImage().getUrl())
                    .into(ivCoverPhoto);
            location = geoPointToString(event.get("location").toString());
            addOnClickListners();
        }
    }

    private void addOnClickListners() {
        tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                String data = String.format("%s?q=%s", location, event.getName());
                intent.setData(Uri.parse(data));
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }
    private String geoPointToString(String geopoint) {
        String temp = geopoint.substring(geopoint.indexOf('[') + 1, geopoint.length() - 1);
        return "geo:" + temp;
    }
}
