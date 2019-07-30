package com.codepath.travelapp.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.travelapp.Activities.MainActivity;
import com.codepath.travelapp.Fragments.EventDetailsFragment;
import com.codepath.travelapp.Models.DayPlan;
import com.codepath.travelapp.R;
import com.parse.ParseException;

import java.util.ArrayList;

public class DayPlanEditableAdapter extends RecyclerView.Adapter<DayPlanEditableAdapter.ViewHolder> {

    private Context context;
    private ArrayList<DayPlan> dayPlans;


    public DayPlanEditableAdapter(ArrayList<DayPlan> dayPlans) {
        this.dayPlans = dayPlans;
    }

    @NonNull
    @Override
    public DayPlanEditableAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_editable_day_plan, parent, false);
        return new DayPlanEditableAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayPlanEditableAdapter.ViewHolder holder, int position) {
        // Get the current day
        final DayPlan dayPlan = dayPlans.get(position);

        //Set the views
        holder.tvDayTitle.setText(dayPlan.getDate().toString());
        try { // Try/catch needed to handle except for '.fetchIfNeeded()'
            if (dayPlan.getMorningEvent() != null) {
                holder.tvMorningName.setText(dayPlan.getMorningEvent().fetchIfNeeded().getString("name"));
                Glide.with(context)
                        .load(dayPlan.getMorningEvent().getImage().getUrl())
                        .into(holder.ivMorningImage);

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
                holder.ivEditMorningEvent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO MAKE EVENT EDITABLE
                        Toast.makeText(context, "Edit morning event", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                // TODO place holder
            }

            if (dayPlan.getAfternoonEvent() != null) {
                holder.tvAfternoonName.setText(dayPlan.getAfternoonEvent().fetchIfNeeded().getString("name"));
                Glide.with(context)
                        .load(dayPlan.getAfternoonEvent().getImage().getUrl())
                        .into(holder.ivAfternoonImage);

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

                holder.ivEditAfternoonEvent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO MAKE EVENT EDITABLE
                        Toast.makeText(context, "Edit afternoon event", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                // TODO place holder
            }

            if (dayPlan.getEveningEvent() != null) {
                holder.tvEveningName.setText(dayPlan.getEveningEvent().fetchIfNeeded().getString("name"));
                Glide.with(context)
                        .load(dayPlan.getEveningEvent().getImage().getUrl())
                        .into(holder.ivEveningImage);

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

                holder.ivEditEveningEvent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO MAKE EVENT EDITABLE
                        Toast.makeText(context, "Edit evening event", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                // TODO place holder
            }
        } catch (ParseException e) {
            e.printStackTrace();
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
        private ImageView ivEditMorningEvent;
        private ImageView ivEditAfternoonEvent;
        private ImageView ivEditEveningEvent;

        ViewHolder(View itemView) {
            super(itemView);
            tvDayTitle = itemView.findViewById(R.id.tvDayTitleEdit);
            cvMorning = itemView.findViewById(R.id.cvMorningEdit);
            cvAfternoon = itemView.findViewById(R.id.cvAfternoonEdit);
            cvEvening = itemView.findViewById(R.id.cvEveningEdit);
            tvMorningName = itemView.findViewById(R.id.tvMorningNameEdit);
            tvAfternoonName = itemView.findViewById(R.id.tvAfternoonNameEdit);
            tvEveningName = itemView.findViewById(R.id.tvEveningNameEdit);
            ivMorningImage = itemView.findViewById(R.id.ivMorningImageEdit);
            ivAfternoonImage = itemView.findViewById(R.id.ivAfternoonImageEdit);
            ivEveningImage = itemView.findViewById(R.id.ivEveningImageEdit);
            ivEditMorningEvent = itemView.findViewById(R.id.ivEditMorningEvent);
            ivEditAfternoonEvent = itemView.findViewById(R.id.ivEditAfternoonEvent);
            ivEditEveningEvent = itemView.findViewById(R.id.ivEditEveningEvent);
        }
    }

}
