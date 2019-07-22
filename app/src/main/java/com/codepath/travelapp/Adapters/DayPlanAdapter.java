package com.codepath.travelapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.travelapp.Models.DayPlan;
import com.codepath.travelapp.Models.Event;
import com.codepath.travelapp.Models.Trip;
import com.codepath.travelapp.R;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.List;

public class DayPlanAdapter extends RecyclerView.Adapter<DayPlanAdapter.ViewHolder> {

    private Context context;
    private ArrayList<DayPlan> dayPlans;

    public DayPlanAdapter(ArrayList<DayPlan> dayPlans) {
        this.dayPlans = dayPlans;
    }

    @NonNull
    @Override
    public DayPlanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new DayPlanAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayPlanAdapter.ViewHolder holder, int position) {
        //Event event = dayPlans.get(position);
    }

    @Override
    public int getItemCount() {
        return dayPlans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvEventName;
        private ImageView ivEventTile;

        ViewHolder(View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            ivEventTile = itemView.findViewById(R.id.ivEventTile);
        }
    }

    public void clear() {
       dayPlans.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<DayPlan> list) {
        dayPlans.addAll(list);
        notifyDataSetChanged();
    }
}
