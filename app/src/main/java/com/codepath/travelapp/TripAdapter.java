package com.codepath.travelapp;

import android.content.Context;
import android.graphics.PostProcessor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.travelapp.Models.Trip;
import com.parse.ParseFile;

import java.util.ArrayList;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Trip> trips;

    public TripAdapter(ArrayList<Trip> trips) {
        this.trips = trips;
    }


    @NonNull
    @Override
    public TripAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_post,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trip trip = trips.get(position);
        holder.tvTripBudget.setText(trip.getBudget().toString());
        holder.tvTripDates.setText(trip.getStartDate().toString());
        holder.tvTripName.setText(trip.getName());
        ParseFile image = trip.getImage();
        if (image != null) {
            Glide.with(context)
                    .load(image.getUrl())
                    .into(holder.ivTripImage);
        }

    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
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
        }
    }
}
