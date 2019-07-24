package com.codepath.travelapp.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.codepath.travelapp.MainActivity;
import com.codepath.travelapp.Models.Trip;
import com.codepath.travelapp.R;
import com.codepath.travelapp.fragments.ProfileFragment;
import com.codepath.travelapp.fragments.TripDetailsFragment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Trip> trips;
    private String username;
    private String APP_TAG = "TripAdapter";


    public TripAdapter(ArrayList<Trip> trips) {
        this.trips = trips;
    }

    @NonNull
    @Override
    public TripAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Trip trip = trips.get(position);
        holder.tvTripBudget.setText(trip.getBudget().toString());
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

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTripDates = itemView.findViewById(R.id.tvTripDates);
            tvTripBudget = itemView.findViewById(R.id.tvTripBudget);
            tvTripName = itemView.findViewById(R.id.tvTripName);
            ivTripImage = itemView.findViewById(R.id.ivTripImage);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);

            itemView.setOnClickListener(this);
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

    public void clear() {
        trips.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Trip> list) {
        trips.addAll(list);
        notifyDataSetChanged();
    }
//    private void sendUserBundle(String username) {
//        Fragment fragment = new ProfileFragment();
//
//        ParseQuery<ParseUser> userQuery = new ParseQuery<>(ParseUser.class);
//        userQuery.whereEqualTo("username", username);
//        userQuery.findInBackground(new FindCallback<ParseUser>() {
//            @Override
//            public void done(List<ParseUser> objects, ParseException e) {
//                if (e != null) {
//                    Log.e(APP_TAG, "Error");
//                    e.printStackTrace();
//                    return;
//                }
//                Log.d("ComposeFragment", objects.toString());
//                user = (ParseUser) objects.get(0);
//            }
//        });
//        Bundle userBundle = new Bundle();
//        userBundle.putParcelable("user",  user);
//        fragment.setArguments(userBundle);
//
//        MainActivity.fragmentManager.beginTransaction()
//                .replace(R.id.flContainer, fragment)
//                .addToBackStack(null)
//                .commit();
//
//    }

}
