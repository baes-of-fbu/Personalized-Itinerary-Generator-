package com.codepath.travelapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.travelapp.Activities.MainActivity;
import com.codepath.travelapp.Fragments.CommentDialogFragment;
import com.codepath.travelapp.Fragments.ProfileFragment;
import com.codepath.travelapp.Fragments.TripDetailsFragment;
import com.codepath.travelapp.Models.City;
import com.codepath.travelapp.Models.Trip;
import com.codepath.travelapp.Models.User;
import com.codepath.travelapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.codepath.travelapp.R.drawable.heart_filled;
import static com.codepath.travelapp.R.drawable.save_filled;
import static com.codepath.travelapp.R.drawable.ufi_heart;
import static com.codepath.travelapp.R.drawable.ufi_save;


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

        final Trip trip = trips.get(position);
        final ParseObject[] savedObject = new ParseObject[1];
        final boolean[] savedCurrent = {false};
        final boolean[] likedCurrent = {false};
        final int[] numLikes = {0};

        final String[] costString = {"$" + trip.getCost().toString()};
        holder.tvTripCost.setText(costString[0]);
        holder.tvTripDates.setText(trip.getNumDays().toString());
        holder.tvTripName.setText(trip.getName());
        holder.tvUsername.setText(trip.getOwner().getUsername());

        // Load trip owner profile image
        if (trip.getOwner().get("profileImage") != null) {
            ParseFile image = (ParseFile) trip.getOwner().get("profileImage");
            assert image != null;
            Glide.with(context)
                    .load(image.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.ivProfileImage);
        }

        // Load trip cover photo
        if (trip.getImage() != null) {
            ParseFile image = trip.getImage();
            assert image != null;
            Glide.with(context)
                    .load(image.getUrl())
                    .into(holder.ivTripImage);
        }

        /*
         * Trip does not store the complete City object, so a Parse query must be made to get the
         * object, allowing access to city.getName() and city.getState()
         */
        String cityId = trip.getCity().getObjectId();
        ParseQuery<City> cityQuery = new ParseQuery<>(City.class);
        cityQuery.whereEqualTo(City.KEY_OBJECT_ID, cityId);
        cityQuery.findInBackground(new FindCallback<City>() {
            @Override
            public void done(List<City> objects, ParseException e) {
                if (e == null) {
                    city = objects.get(0);
                    String cityStateString = city.getName() + ", " + city.getState();
                    holder.tvCityName.setText(cityStateString);
                } else {
                    e.printStackTrace();
                }
            }
        });

        // Send a Parse Query to get "likes" Relation
        trip.getLikes().getQuery().findInBackground(new FindCallback<User>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void done(List<User> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        if (objects.get(i).getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                            likedCurrent[0] = true;
                        }
                    }
                    if (likedCurrent[0]) {
                        setActiveLikeIcon(holder);
                    } else {
                        setInactiveLikeIcon(holder);
                    }
                    numLikes[0] = objects.size();
                    holder.tvNumLikes.setText(Integer.toString(numLikes[0]));
                } else {
                    e.printStackTrace();
                }
            }
        });

        // Set onClickListener to Like icon to reflect whether or not the current user has liked a trip
        holder.ibLike.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if (likedCurrent[0]) {
                    trip.getLikes().remove((User) ParseUser.getCurrentUser());
                    numLikes[0]--;
                    setInactiveLikeIcon(holder);
                } else {
                    trip.getLikes().add((User) ParseUser.getCurrentUser());
                    numLikes[0]++;
                    setActiveLikeIcon(holder);
                }
                likedCurrent[0] = !likedCurrent[0];
                trip.saveInBackground();
                holder.tvNumLikes.setText(Integer.toString(numLikes[0]));
            }
        });

        // Send a Parse Query to get "saved" relation
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("SavedTrip");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.whereEqualTo("trip", trip);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> savedList, ParseException e) {
                if (e == null) {
                    if (savedList.size() == 1) {
                        savedObject[0] = savedList.get(0);
                        savedCurrent[0] = true;
                        setActiveSaveIcon(holder);
                    } else {
                        setInactiveSaveIcon(holder);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });


        // Set onClickListener to Save icon to reflect whether or not the current user has saved the trip
        holder.ibSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (savedCurrent[0]) {
                    savedObject[0].deleteInBackground();
                    setInactiveSaveIcon(holder);
                } else {
                    ParseObject save = new ParseObject("SavedTrip");
                    save.put("trip", trip);
                    save.put("user", ParseUser.getCurrentUser());
                    save.saveInBackground();
                    savedObject[0] = save;
                    setActiveSaveIcon(holder);
                }
                savedCurrent[0] = !savedCurrent[0];
            }
        });


        // Set onClickListener to prompt the current user to write a comment for a trip
        holder.ibComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new CommentDialogFragment();
                FragmentManager fragmentManager = MainActivity.fragmentManager;
                CommentDialogFragment commentDialogFragment = CommentDialogFragment.newInstance(trip);
                commentDialogFragment.show(fragmentManager, "fragment_comment_dialog");
            }
        });

        // Sends a bundle to ProfileFragment when username is clicked
        holder.tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = (String) holder.tvUsername.getText();

                Fragment fragment;
                if (username.equals(ParseUser.getCurrentUser().getUsername())) {
                    fragment = new ProfileFragment();
                    Bundle finalBundle = new Bundle();
                    finalBundle.putString("username", User.getCurrentUser().getUsername());
                    fragment.setArguments(finalBundle);
                    MainActivity.bottomNavigationView.setSelectedItemId(R.id.action_profile);
                } else {
                    fragment = new ProfileFragment();
                    Bundle userBundle = new Bundle();
                    userBundle.putString("username", username);
                    fragment.setArguments(userBundle);
                }

                MainActivity.fragmentManager.beginTransaction()
                        .replace(R.id.flContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Opens city location in Maps application
        holder.tvCityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMaps();
            }
        });
        holder.ivPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMaps();
            }
        });
    }

    private void openMaps() {
        String location = geoPointToString((Objects.requireNonNull(city.get("location"))).toString());
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        String data = String.format("%s?q=%s", location, city.getName());
        intent.setData(Uri.parse(data));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    private void setInactiveLikeIcon(@NonNull ViewHolder holder) {
        holder.ibLike.setImageResource(ufi_heart);
        holder.ibLike.setColorFilter(Color.BLACK);
    }

    private void setActiveLikeIcon(@NonNull ViewHolder holder) {
        holder.ibLike.setImageResource(heart_filled);
        holder.ibLike.setColorFilter(Color.rgb(255, 0, 0));
    }

    private void setInactiveSaveIcon(@NonNull ViewHolder holder) {
        holder.ibSave.setImageResource(ufi_save);
        holder.ibSave.setColorFilter(Color.BLACK);
    }

    private void setActiveSaveIcon(@NonNull ViewHolder holder) {
        holder.ibSave.setImageResource(save_filled);
    }

    private String geoPointToString(String geoPoint) {
        String temp = geoPoint.substring(geoPoint.indexOf('[') + 1, geoPoint.length() - 1);
        return "geo:" + temp;
    }

    // Returns total count of trips
    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvTripCost;
        private TextView tvTripDates;
        private ImageView ivTripImage;
        private TextView tvTripName;
        private TextView tvUsername;
        private ImageView ivProfileImage;
        private TextView tvCityName;
        private ImageView ivPin;
        private TextView tvNumLikes;
        private ImageButton ibLike;
        private ImageButton ibComment;
        private ImageButton ibSave;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Finds views that will be populated
            tvTripDates = itemView.findViewById(R.id.tvTripDates);
            tvTripCost = itemView.findViewById(R.id.tvTripCost);
            tvTripName = itemView.findViewById(R.id.tvTripName);
            ivTripImage = itemView.findViewById(R.id.ivTripImage);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvCityName = itemView.findViewById(R.id.tvCityName);
            ivPin = itemView.findViewById(R.id.ivLocationPin);
            tvNumLikes = itemView.findViewById(R.id.tvNumLikes);
            ibLike = itemView.findViewById(R.id.ibLike);
            ibComment = itemView.findViewById(R.id.ibComment);
            ibSave = itemView.findViewById(R.id.ibSave);

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

    public void addAll(List<Trip> list) {
        trips.addAll(list);
        notifyDataSetChanged();
    }
}
