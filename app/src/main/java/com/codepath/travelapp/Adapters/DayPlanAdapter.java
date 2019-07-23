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
import com.parse.ParseException;
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_day_plan, parent, false);
        return new DayPlanAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayPlanAdapter.ViewHolder holder, int position) {
        DayPlan dayPlan = dayPlans.get(position);
        holder.tvDayTitle.setText(dayPlan.getDate().toString());
        try {
            holder.tvMorningName.setText(dayPlan.getMorningEvent().fetchIfNeeded().getString("name"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            holder.tvAfternoonName.setText(dayPlan.getAfternoonEvent().fetchIfNeeded().getString("name"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            holder.tvEveningName.setText(dayPlan.getEveningEvent().fetchIfNeeded().getString("name"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return dayPlans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDayTitle;
        private TextView tvMorningName;
        private ImageView ivMorningImage;
        private TextView tvAfternoonName;
        private ImageView ivAfternoonImage;
        private TextView tvEveningName;
        private ImageView ivEveningImage;

        ViewHolder(View itemView) {
            super(itemView);
            tvDayTitle = itemView.findViewById(R.id.tvDayTitle);
            tvMorningName = itemView.findViewById(R.id.tvMorningName);
            tvAfternoonName = itemView.findViewById(R.id.tvAfternoonName);
            tvEveningName = itemView.findViewById(R.id.tvEveningName);
            ivMorningImage = itemView.findViewById(R.id.ivMorningImage);
            ivAfternoonImage = itemView.findViewById(R.id.ivAfternoonImage);
            ivEveningImage = itemView.findViewById(R.id.ivEveningImage);
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
