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
import com.codepath.travelapp.Activities.MainActivity;
import com.codepath.travelapp.Models.DayPlan;
import com.codepath.travelapp.R;
import com.codepath.travelapp.Fragments.EventDetailsFragment;
import com.parse.ParseException;

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
        // Get the current day
        final DayPlan dayPlan = dayPlans.get(position);

        //Set the views
        holder.tvDayTitle.setText(dayPlan.getDate().toString());
        try {
            holder.tvMorningName.setText(dayPlan.getMorningEvent().fetchIfNeeded().getString("name"));
            Glide.with(context)
                    .load(dayPlan.getMorningEvent().getImage().getUrl())
                    .into(holder.ivMorningImage);

            holder.tvAfternoonName.setText(dayPlan.getAfternoonEvent().fetchIfNeeded().getString("name"));
            Glide.with(context)
                    .load(dayPlan.getAfternoonEvent().getImage().getUrl())
                    .into(holder.ivAfternoonImage);

            holder.tvEveningName.setText(dayPlan.getEveningEvent().fetchIfNeeded().getString("name"));
            Glide.with(context)
                    .load(dayPlan.getEveningEvent().getImage().getUrl())
                    .into(holder.ivEveningImage);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.cvMorning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new EventDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("event", dayPlan.getMorningEvent());
                fragment.setArguments(bundle);

                MainActivity.fragmentManager.beginTransaction()
                        .replace(R.id.flContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        holder.cvAfternoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new EventDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("event", dayPlan.getAfternoonEvent());
                fragment.setArguments(bundle);

                MainActivity.fragmentManager.beginTransaction()
                        .replace(R.id.flContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        holder.cvEvening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new EventDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("event", dayPlan.getEveningEvent());
                fragment.setArguments(bundle);

                MainActivity.fragmentManager.beginTransaction()
                        .replace(R.id.flContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    // Returns the total count of dayPlans
    @Override
    public int getItemCount() {
        return dayPlans.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDayTitle;
        private CardView cvMorning;
        private TextView tvMorningName;
        private ImageView ivMorningImage;
        private CardView cvAfternoon;
        private TextView tvAfternoonName;
        private ImageView ivAfternoonImage;
        private CardView cvEvening;
        private TextView tvEveningName;
        private ImageView ivEveningImage;

        ViewHolder(View itemView) {
            super(itemView);
            tvDayTitle = itemView.findViewById(R.id.tvDayTitle);
            cvMorning = itemView.findViewById(R.id.cvMorning);
            cvAfternoon = itemView.findViewById(R.id.cvAfternoon);
            cvEvening = itemView.findViewById(R.id.cvEvening);
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
