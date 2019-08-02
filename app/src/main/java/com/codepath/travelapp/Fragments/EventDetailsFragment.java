package com.codepath.travelapp.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.codepath.travelapp.Models.Event;
import com.codepath.travelapp.R;

import java.util.Objects;

public class EventDetailsFragment extends Fragment {

    private TextView tvAddress;
    private String location;
    private Event event;
    private TextView tvWebsite;
    private Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView ivCoverPhoto = view.findViewById(R.id.ivCoverPhoto);
        TextView tvCost = view.findViewById(R.id.tvCost);
        RatingBar rbRating = view.findViewById(R.id.rbRating);
        tvAddress = view.findViewById(R.id.tvAddress);
        TextView tvDescription = view.findViewById(R.id.tvDescription);
        tvWebsite = view.findViewById(R.id.tvWebsite);
        toolbar = view.findViewById(R.id.tbEventDetails);

        Bundle bundle = getArguments();
        if (bundle != null) {
            event = bundle.getParcelable("event");
        }

        if (event != null) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
            toolbar.setTitle(event.getName());

            String cost = "$" + event.getCost().toString();
            tvCost.setText(cost);
            rbRating.setRating(event.getRating().floatValue());

            SpannableString address = new SpannableString(event.getAddress());
            address.setSpan(new UnderlineSpan(), 0, address.length(), 0);
            tvAddress.setText(address);
            tvDescription.setText(event.getDescription());
            SpannableString website = new SpannableString(event.getWebsite());
            website.setSpan(new UnderlineSpan(), 0, website.length(), 0);
            tvWebsite.setText(website);

            Glide.with(Objects.requireNonNull(getContext()))
                    .load(event.getImage().getUrl())
                    .into(ivCoverPhoto);
            location = geoPointToString((Objects.requireNonNull(event.get("location"))).toString());
            addOnClickListeners();
        }
    }

    private void addOnClickListeners() {
        tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                String data = String.format("%s?q=%s", location, event.getName());
                intent.setData(Uri.parse(data));
                if (intent.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        tvWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getWebsite()));
                if (browserIntent.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager()) != null) {
                    startActivity(browserIntent);
                }
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    private String geoPointToString(String geoPoint) {
        String temp = geoPoint.substring(geoPoint.indexOf('[') + 1, geoPoint.length() - 1);
        return "geo:" + temp;
    }
}
