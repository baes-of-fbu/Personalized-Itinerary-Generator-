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
import com.codepath.travelapp.Models.Event;
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
        final DayPlan dayPlan = dayPlans.get(position);
        holder.tvDayTitle.setText(dayPlan.getDate().toString().substring(0,10));

        if (dayPlan.getMorningEvent() == null) {
            // Fills morning card view with empty event and removes onClickListener
            useEmptyEvent(holder.tvMorningName, holder.ivMorningImage, holder.tvMorningEventCost);
            holder.cvMorning.setOnClickListener(null);
        } else {

            // Try/catch needed for .fetchIfNeeded()
            try {
                holder.tvMorningName.setText(dayPlan.getMorningEvent().fetchIfNeeded().getString(Event.KEY_NAME));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Adds morning event cost
            int cost = (int) dayPlan.getMorningEvent().getCost();
            if (cost == 0) {
                holder.tvMorningEventCost.setText(context.getResources().getString(R.string.free));
                holder.tvMorningEventCost.setTextColor(context.getResources().getColor(R.color.LightSkyBlue));
            } else {
                holder.tvMorningEventCost.setText(String.format("$%s", String.valueOf(cost)));
                holder.tvMorningEventCost.setTextColor(context.getResources().getColor(R.color.black));
            }

            // Adds morning event image
            Glide.with(context)
                    .load(dayPlan.getMorningEvent().getImage().getUrl())
                    .into(holder.ivMorningImage);

            // Adds listener to morning card view
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
        }

        if (dayPlan.getAfternoonEvent() == null) {
            // Fills afternoon card view with empty event and removes onClickListener
            useEmptyEvent(holder.tvAfternoonName, holder.ivAfternoonImage, holder.tvAfternoonEventCost);
            holder.cvAfternoon.setOnClickListener(null);
        } else {

            // Try/catch needed for .fetchIfNeeded()
            try {
                holder.tvAfternoonName.setText(dayPlan.getAfternoonEvent().fetchIfNeeded().getString(Event.KEY_NAME));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Adds afternoon event cost
            int cost = (int) dayPlan.getAfternoonEvent().getCost();
            if (cost == 0) {
                holder.tvAfternoonEventCost.setText(context.getResources().getString(R.string.free));
                holder.tvAfternoonEventCost.setTextColor(context.getResources().getColor(R.color.LightSkyBlue));
            } else {
                holder.tvAfternoonEventCost.setText(String.format("$%s", String.valueOf(cost)));
                holder.tvAfternoonEventCost.setTextColor(context.getResources().getColor(R.color.black));
            }

            // Adds afternoon event image
            Glide.with(context)
                    .load(dayPlan.getAfternoonEvent().getImage().getUrl())
                    .into(holder.ivAfternoonImage);

            // Adds listener to afternoon card view
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
        }

        if (dayPlan.getEveningEvent() == null) {
            // Fills evening card view with empty event and removes onClickListener
            useEmptyEvent(holder.tvEveningName, holder.ivEveningImage, holder.tvEveningEventCost);
            holder.cvEvening.setOnClickListener(null);
        } else {

            // Try/catch needed for .fetchIfNeeded()
            try {
                holder.tvEveningName.setText(dayPlan.getEveningEvent().fetchIfNeeded().getString(Event.KEY_NAME));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Adds evening event cost
            int cost = (int) dayPlan.getEveningEvent().getCost();
            if (cost == 0) {
                holder.tvEveningEventCost.setText(context.getResources().getString(R.string.free));
                holder.tvEveningEventCost.setTextColor(context.getResources().getColor(R.color.LightSkyBlue));
            } else {
                holder.tvEveningEventCost.setText(String.format("$%s", String.valueOf(cost)));
                holder.tvEveningEventCost.setTextColor(context.getResources().getColor(R.color.black));
            }

            // Adds evening event image
            Glide.with(context)
                    .load(dayPlan.getEveningEvent().getImage().getUrl())
                    .into(holder.ivEveningImage);

            // Adds listener to evening card view
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
        private TextView tvMorningEventCost;
        private TextView tvAfternoonEventCost;
        private TextView tvEveningEventCost;

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
            tvMorningEventCost = itemView.findViewById(R.id.tvMorningEventCost);
            tvAfternoonEventCost = itemView.findViewById(R.id.tvAfternoonEventCost);
            tvEveningEventCost = itemView.findViewById(R.id.tvEveningEventCost);
        }
    }

    private void useEmptyEvent(TextView tvEventName, ImageView ivEventImage, TextView tvEventCost) {
        tvEventName.setText(R.string.empty_slot);
        ivEventImage.setImageResource(R.drawable.emptyevent);
        tvEventCost.setText("");
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
