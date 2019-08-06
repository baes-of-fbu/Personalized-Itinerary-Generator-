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
import com.codepath.travelapp.Activities.MainActivity;
import com.codepath.travelapp.Fragments.TripDetailsFragment;
import com.codepath.travelapp.Models.Trip;
import com.codepath.travelapp.R;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.List;

public class ProfileTripsAdapter extends RecyclerView.Adapter<ProfileTripsAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Trip> trips;

    public ProfileTripsAdapter(ArrayList<Trip> trips) {
        this.trips = trips;
    }

    @NonNull
    @Override
    public ProfileTripsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_post_profile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileTripsAdapter.ViewHolder holder, int position) {
        Trip trip = trips.get(position);

        if (trip != null) {
            String budgetString = "$" + trip.getBudget().toString();
            holder.tvTripBudget.setText(budgetString);
            holder.tvTripName.setText(trip.getName());
            holder.tvTripBudget.setText(trip.getBudget().toString());

            if (trip.get("image") != null) {
                ParseFile image = (ParseFile) trip.get("image");
                assert image != null;
                Glide.with(context)
                        .load(image.getUrl())
                        .into(holder.ivTripImage);
            }
        }
    }

    // Return the total count of trips
    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvTripBudget;
        private ImageView ivTripImage;
        private TextView tvTripName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTripBudget = itemView.findViewById(R.id.tvTripCost);
            tvTripName = itemView.findViewById(R.id.tvTripName);
            ivTripImage = itemView.findViewById(R.id.ivTripImage);

            itemView.setOnClickListener (this);
        }

        @Override
        public void onClick(View v) {
            // Sends trip info to TripDetailsFragment when trip card is clicked
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

    public void clear(){
        trips.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Trip> list) {
        trips.addAll(list);
        notifyDataSetChanged();
    }
}
