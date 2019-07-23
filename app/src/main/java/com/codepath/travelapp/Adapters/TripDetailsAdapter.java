package com.codepath.travelapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travelapp.Models.DayPlan;
import com.codepath.travelapp.Models.Trip;
import com.codepath.travelapp.R;

import java.util.ArrayList;
import java.util.List;

public class TripDetailsAdapter extends RecyclerView.Adapter<TripDetailsAdapter.ViewHolder> {
    private Context context;
    private ArrayList<DayPlan> dayPlans;

    public TripDetailsAdapter(ArrayList<DayPlan> dayPlans) {
        this.dayPlans = dayPlans;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_day_plan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DayPlanAdapter adapter = new DayPlanAdapter(dayPlans);
        holder.rvSchedule.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {return dayPlans.size();}

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView rvSchedule;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rvSchedule = itemView.findViewById(R.id.rvSchedule);
        }
    }
    public void clear(){
        dayPlans.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<DayPlan> list) {
        dayPlans.addAll(list);
        notifyDataSetChanged();
    }
}
