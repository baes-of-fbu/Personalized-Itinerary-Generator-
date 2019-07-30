package com.codepath.travelapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.codepath.travelapp.Models.City;
import com.codepath.travelapp.Models.Trip;
import com.codepath.travelapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Trip> trips;
    private String username;
    private City city;

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

        Trip trip = trips.get(position);

        String budgetString = "$" + trip.getBudget().toString();
        holder.tvTripBudget.setText(budgetString);
        holder.tvTripDates.setText(trip.getNumDays().toString());
        holder.tvTripName.setText(trip.getName());
        holder.tvUsername.setText(trip.getOwner().getUsername());

        // Trip does not store the complete City object, so a Parse
        // query must be made to get the object, allowing access to
        // city.getName() and city.getState()
        String cityId = trip.getCity().getObjectId();
        ParseQuery<City> cityQuery = new ParseQuery<City>(City.class);
        cityQuery.whereEqualTo(City.KEY_OBJECT_ID, cityId);
        cityQuery.findInBackground(new FindCallback<City>() {
            @Override
            public void done(List<City> objects, ParseException e) {
                if (e == null) {
                    city = objects.get(0);
                    String cityStateString = city.getName() + ", " + city.getState();
                    holder.tvCityName.setText(cityStateString);
                }
            }
        });



        if (trip.getOwner().get("profileImage") != null) {
            ParseFile image = (ParseFile) trip.getOwner().get("profileImage"); // TODO CHANGE TO USER, NOT PARSEUSER
            assert image != null;
            Glide.with(context)
                    .load(image.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.ivProfileImage);
        }

        if (trip.getImage() != null) {
            ParseFile image = trip.getImage();
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
        holder.tvCityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String location = geoPointToString((Objects.requireNonNull(city.get("location"))).toString());
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                String data = String.format("%s?q=%s", location,city.getName());
                intent.setData(Uri.parse(data));
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                }
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
        private TextView tvCityName;

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
            tvCityName = itemView.findViewById(R.id.tvCityName);

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
    private String geoPointToString(String geoPoint) {
        String temp = geoPoint.substring(geoPoint.indexOf('[') + 1, geoPoint.length() - 1);
        return "geo:" + temp;
    }

}
