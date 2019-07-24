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

import com.codepath.travelapp.MainActivity;
import com.codepath.travelapp.Models.DayPlan;
import com.codepath.travelapp.R;
import com.codepath.travelapp.fragments.EventDetailsFragment;
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
        holder.tvDayTitle.setText(dayPlan.getDate().toString());
        try {
            holder.tvMorningName.setText(dayPlan.getMorningEvent().fetchIfNeeded().getString("name"));
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
        private CardView cvMorning;
        private TextView tvMorningName;
        private ImageView ivMorningImage;
        private TextView tvAfternoonName;
        private ImageView ivAfternoonImage;
        private TextView tvEveningName;
        private ImageView ivEveningImage;

        ViewHolder(View itemView) {
            super(itemView);
            tvDayTitle = itemView.findViewById(R.id.tvDayTitle);
            cvMorning = itemView.findViewById(R.id.cvMorning);
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
