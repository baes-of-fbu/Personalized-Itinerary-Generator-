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
import com.codepath.travelapp.MainActivity;
import com.codepath.travelapp.Models.Trip;
import com.codepath.travelapp.R;
import com.codepath.travelapp.fragments.TripDetailsFragment;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.List;

public class PreviousTripAdapter extends RecyclerView.Adapter<PreviousTripAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Trip> trips;

    public PreviousTripAdapter(ArrayList<Trip> trips) {
        this.trips = trips;
    }

    @NonNull
    @Override
    public PreviousTripAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_post_profile, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviousTripAdapter.ViewHolder holder, int position) {
        Trip trip = (Trip) trips.get(position);
        holder.tvTripBudget.setText(trip.getBudget().toString());
//        holder.tvTripDates.setText(trip.getNumDays().toString());
        holder.tvTripName.setText(trip.getName());
        holder.tvTripBudget.setText(trip.getBudget().toString());


        if (trip.get("image") != null) {
            ParseFile image = (ParseFile) trip.get("image");
            Glide.with(context)
                    .load(image.getUrl())
                    .into(holder.ivTripImage);
        }

    }
    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvTripBudget;
        private TextView tvTripDates;
        private ImageView ivTripImage;
        private TextView tvTripName;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTripDates = itemView.findViewById(R.id.tvTripDates);
            tvTripBudget = itemView.findViewById(R.id.tvTripBudget);
            tvTripName = itemView.findViewById(R.id.tvTripName);
            ivTripImage = itemView.findViewById(R.id.ivTripImage);

            itemView.setOnClickListener((View.OnClickListener) this);
        }

        @Override
        public void onClick(View v) {
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
        notifyDataSetChanged();;
    }

    // Add a list of items -- change to type used
    public void addAll(List<Trip> list) {
        trips.addAll(list);
        notifyDataSetChanged();
    }
}
