package com.codepath.travelapp.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.travelapp.Activities.MainActivity;
import com.codepath.travelapp.Fragments.ProfileFragment;
import com.codepath.travelapp.Models.City;
import com.codepath.travelapp.Models.Event;
import com.codepath.travelapp.Models.User;
import com.codepath.travelapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private Context context;
    private List<Event> events;
    private City city;


    public EventAdapter(ArrayList<Event> events){ this.events = events;}

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_explore_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.tvName.setText(event.getName());
        holder.tvBudget.setText("$" + event.getCost().toString());
        Glide.with(context)
                .load(event.getImage().getUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.ivEvent);
        String cityId = event.getCity().getObjectId();
        ParseQuery<City> cityQuery = new ParseQuery<>(City.class);
        cityQuery.whereEqualTo(City.KEY_OBJECT_ID, cityId);
        cityQuery.findInBackground(new FindCallback<City>() {
            @Override
            public void done(List<City> objects, ParseException e) {
                if (e == null) {
                    city = objects.get(0);
                    String cityStateString = city.getName() + ", " + city.getState();
                    holder.tvCity.setText(cityStateString);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void clear() {
        events.clear();
        notifyDataSetChanged();
    }
    public void addAll(List<Event> list) {
        events.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvBudget;
        TextView tvCity;
        ImageView ivEvent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvBudget = itemView.findViewById(R.id.tvBudget);
            tvCity = itemView.findViewById(R.id.tvCity);
            ivEvent = itemView.findViewById(R.id.ivEvent);
        }

    }

}