package com.codepath.travelapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.travelapp.Adapters.DayPlanAdapter;
import com.codepath.travelapp.Adapters.TripDetailsAdapter;
import com.codepath.travelapp.Models.DayPlan;
import com.codepath.travelapp.Models.Trip;
import com.codepath.travelapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL;
import static com.parse.ParseUser.getCurrentUser;

public class TripDetailsFragment extends Fragment {

    private DayPlanAdapter adapter;
    private ArrayList<DayPlan> mDayPlan;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trip_details,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView ivCoverPhoto = view.findViewById(R.id.ivCoverPhoto);
        ImageView ivProfileImage = view.findViewById(R.id.ivProfileImage);
        TextView tvTripName = view.findViewById(R.id.tvTripName);
        TextView tvUsername = view.findViewById(R.id.tvUsername);
        TextView tvTravelDates = view.findViewById(R.id.tvTravelDates);
        TextView tvDays = view.findViewById(R.id.tvDays);
        TextView tvBudget = view.findViewById(R.id.tvBudget);
        RecyclerView rvSchedule = view.findViewById(R.id.rvSchedule);

        Bundle bundle = getArguments();
        assert bundle != null;
        Trip trip = (Trip) bundle.getSerializable("Trip");

        if (trip != null) {
            tvTripName.setText(trip.getName());
            tvUsername.setText(trip.getOwner().getUsername());
            tvTravelDates.setText(trip.getStartDate() + " - " + trip.getEndDate());
            tvDays.setText(trip.getNumDays().toString());
            tvBudget.setText("$" + trip.getBudget());

            Glide.with(Objects.requireNonNull(getContext()))
                    .load(trip.getImage().getUrl())
                    .into(ivCoverPhoto);

            ParseFile image = (ParseFile) trip.getOwner().get("profileImage");
            assert image != null;
            Glide.with(getContext())
                    .load(image.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfileImage);
        }

        //create the adapter
        mDayPlan = new ArrayList<>();
        //create the data source
        adapter = new DayPlanAdapter(mDayPlan);
        // set the layout manager on the recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), HORIZONTAL, false);
        rvSchedule.setLayoutManager(linearLayoutManager);

        rvSchedule.setAdapter(adapter);

        // TODO change this
        adapter.clear();

        ParseQuery<DayPlan> dayPlanQuery = new ParseQuery<DayPlan>(DayPlan.class);

        dayPlanQuery.setLimit(10);
        dayPlanQuery.include(DayPlan.KEY_TRIP);
        dayPlanQuery.findInBackground(new FindCallback<DayPlan>() {
            @Override
            public void done(List<DayPlan> dayPlans, ParseException e) {
                if (e != null) {
                    Log.e("DayPlan","Error");
                    e.printStackTrace();
                    return;
                }
                adapter.addAll(dayPlans);
            }
        });
    }
}
