package com.codepath.travelapp.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.travelapp.Activities.MainActivity;
import com.codepath.travelapp.Fragments.ProfileFragment;
import com.codepath.travelapp.Fragments.TripDetailsFragment;
import com.codepath.travelapp.Models.Trip;
import com.codepath.travelapp.R;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.List;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Trip> trips;
    private String username;

    public TimelineAdapter(ArrayList<Trip> trips) {
        this.trips = trips;
    }

    @NonNull
    @Override
    public TimelineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // Get a trip
        Trip trip = trips.get(position);
        // Set views for this trip
        holder.tvTripBudget.setText("$" + trip.getBudget().toString());

        holder.tvTripDates.setText(trip.getNumDays().toString());

        holder.tvTripName.setText(trip.getName());

        holder.tvUsername.setText(trip.getOwner().getUsername());

        if (trip.getOwner().get("profileImage") != null) {
            ParseFile image = (ParseFile) trip.getOwner().get("profileImage");
            assert image != null;
            Glide.with(context)
                    .load(image.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.ivProfileImage);
        }

        if (trip.get("image") != null) {
            ParseFile image = (ParseFile) trip.get("image");
            assert image != null;
            Glide.with(context)
                    .load(image.getUrl())
                    .into(holder.ivTripImage);
        }
        // Sends a bundle to ProfileFragment when username is clicked
        holder.tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = (String) holder.tvUsername.getText();
                Fragment fragment = new ProfileFragment();
                Bundle userBundle = new Bundle();
                userBundle.putString("username",  username);
                fragment.setArguments(userBundle);

                MainActivity.fragmentManager.beginTransaction()
                        .replace(R.id.flContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    // Returns total count of trips
    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvTripBudget;
        private TextView tvTripDates;
        private ImageView ivTripImage;
        private TextView tvTripName;
        private TextView tvUsername;
        private ImageView ivProfileImage;
        private TextView tvTags;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Finds views that will be populated
            tvTripDates = itemView.findViewById(R.id.tvTripDates);
            tvTripBudget = itemView.findViewById(R.id.tvTripBudget);
            tvTripName = itemView.findViewById(R.id.tvTripName);
            ivTripImage = itemView.findViewById(R.id.ivTripImage);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvTags = itemView.findViewById(R.id.tvTags);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Sends a bundle to TripDetailsFragment when trip item is clicked
            Log.d("Adapter", "item clicked");
            final Trip trip = trips.get(getAdapterPosition());
            if (trip != null) {
                Fragment fragment = new TripDetailsFragment();

                Bundle bundle = new Bundle();
                bundle.putSerializable("Trip", trip);
                fragment.setArguments(bundle);

                MainActivity.fragmentManager.beginTransaction()
                        .replace(R.id.flContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    public void clear() {
        trips.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Trip> list) {
        trips.addAll(list);
        notifyDataSetChanged();
    }

}
