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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.travelapp.Activities.MainActivity;
import com.codepath.travelapp.Fragments.EditEventDialogFragment;
import com.codepath.travelapp.Fragments.EventDetailsFragment;
import com.codepath.travelapp.Models.DayPlan;
import com.codepath.travelapp.Models.Event;
import com.codepath.travelapp.R;
import com.parse.ParseException;

import java.util.ArrayList;

public class DayPlanEditableAdapter extends RecyclerView.Adapter<DayPlanEditableAdapter.ViewHolder> implements EditEventDialogFragment.Listener {

    private Context context;
    private String timeOfDay;
    private DayPlan currDayPlan;
    private ArrayList<DayPlan> dayPlans;
    private ArrayList<Event> allAvailableEvents;
    private ViewHolder currHolder;

    private String morningEvent;
    private String afternoonEvent;
    private String eveningEvent;
    private String cancelEdit;
    private String removeEvent;
    private String reGenerateEvent;


    public DayPlanEditableAdapter(ArrayList<DayPlan> dayPlans, ArrayList<Event> allAvailableEvents) {
        this.dayPlans = dayPlans;
        this.allAvailableEvents = allAvailableEvents;
    }

    @NonNull
    @Override
    public DayPlanEditableAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        morningEvent = context.getResources().getString(R.string.morning_event);
        afternoonEvent = context.getResources().getString(R.string.afternoon_event);
        eveningEvent = context.getResources().getString(R.string.evening_event);
        cancelEdit = context.getResources().getString(R.string.cancel);
        removeEvent = context.getResources().getString(R.string.remove_event);
        reGenerateEvent = context.getResources().getString(R.string.regenerate_event);

        View view = LayoutInflater.from(context).inflate(R.layout.item_editable_day_plan, parent, false);
        return new DayPlanEditableAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayPlanEditableAdapter.ViewHolder holder, int position) {
        // Get the current day
        final DayPlan dayPlan = dayPlans.get(position);
        holder.tvDayTitle.setText(dayPlan.getDate().toString());

        if (dayPlan.getMorningEvent() == null) {
            // Fills morning card view with empty event and removes onClickListener
            useEmptyEvent(holder.tvMorningName, holder.ivMorningImage, holder.tvMorningPrice);
        } else {

            // Try/catch needed for .fetchIfNeeded()
            try {
                holder.tvMorningName.setText(dayPlan.getMorningEvent().fetchIfNeeded().getString("name"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Adds morning event cost
            int cost = (int) dayPlan.getMorningEvent().getCost();
            if (cost == 0) {
                holder.tvMorningPrice.setText(context.getResources().getString(R.string.free));
                holder.tvMorningPrice.setTextColor(context.getResources().getColor(R.color.LightSkyBlue));
            } else {
                holder.tvMorningPrice.setText(String.format("$%s", String.valueOf(cost)));
                holder.tvMorningPrice.setTextColor(context.getResources().getColor(R.color.black));
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
            useEmptyEvent(holder.tvAfternoonName, holder.ivAfternoonImage, holder.tvAfternoonPrice);
        } else {

            // Try/catch needed for .fetchIfNeeded()
            try {
                holder.tvAfternoonName.setText(dayPlan.getAfternoonEvent().fetchIfNeeded().getString("name"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Adds afternoon event cost
            int cost = (int) dayPlan.getAfternoonEvent().getCost();
            if (cost == 0) {
                holder.tvAfternoonPrice.setText(context.getResources().getString(R.string.free));
                holder.tvAfternoonPrice.setTextColor(context.getResources().getColor(R.color.LightSkyBlue));
            } else {
                holder.tvAfternoonPrice.setText(String.format("$%s", String.valueOf(cost)));
                holder.tvAfternoonPrice.setTextColor(context.getResources().getColor(R.color.black));
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
            useEmptyEvent(holder.tvEveningName, holder.ivEveningImage, holder.tvEveningPrice);
        } else {

            // Try/catch needed for .fetchIfNeeded()
            try {
                holder.tvEveningName.setText(dayPlan.getEveningEvent().fetchIfNeeded().getString("name"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Adds evening event cost
            int cost = (int) dayPlan.getEveningEvent().getCost();
            if (cost == 0) {
                holder.tvEveningPrice.setText(context.getResources().getString(R.string.free));
                holder.tvEveningPrice.setTextColor(context.getResources().getColor(R.color.LightSkyBlue));
            } else {
                holder.tvEveningPrice.setText(String.format("$%s", String.valueOf(cost)));
                holder.tvEveningPrice.setTextColor(context.getResources().getColor(R.color.black));
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
        addEditClickListeners(holder, dayPlan);
    }

    private void addEditClickListeners(final ViewHolder holder, final DayPlan dayPlan) {

        holder.ivEditMorningEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currHolder = holder;
                currDayPlan = dayPlan;
                timeOfDay = morningEvent;
                promptUser();
            }
        });

        holder.ivEditAfternoonEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currHolder = holder;
                currDayPlan = dayPlan;
                timeOfDay = afternoonEvent;
                promptUser();
            }
        });

        holder.ivEditEveningEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currHolder = holder;
                currDayPlan = dayPlan;
                timeOfDay = eveningEvent;
                promptUser();
            }
        });
    }

    // Prompts user with options for editing an event
    // Passes the dayPlan and time of day so that the return data
    // can include this information and use it to edit the arraylist of events
    private void promptUser() {
        FragmentManager fragmentManager = MainActivity.fragmentManager;
        EditEventDialogFragment editEventDialogFragment = EditEventDialogFragment.newInstance();
        editEventDialogFragment.setListener(DayPlanEditableAdapter.this);
        editEventDialogFragment.show(fragmentManager, "fragment_edit_event_options");
    }

    // This is called when the dialog is completed and the results have been passed
    @Override
    public void returnData(String action) {
        if (action.contentEquals(cancelEdit)) {
            // Do nothing
        } else if (action.contentEquals(removeEvent)) {
            // Removes event from the DayPlan
            removeEvent();
            notifyDataSetChanged();
        } else if (action.contentEquals(reGenerateEvent)) {
            // Removes event and regenerates schedule
            removeEvent();
            generateEvent();
        }
    }

    private void removeEvent() {
        if (timeOfDay.contentEquals(morningEvent)) {
            if (currDayPlan.getMorningEvent() != null) {
                allAvailableEvents.add(currDayPlan.getMorningEvent());
                useEmptyEvent(currHolder.tvMorningName, currHolder.ivMorningImage, currHolder.tvMorningPrice);
                currHolder.cvMorning.setOnClickListener(null);
                currDayPlan.removeMorningEvent();
            }
        } else if (timeOfDay.contentEquals(afternoonEvent)) {
            if (currDayPlan.getAfternoonEvent() != null) {
                allAvailableEvents.add(currDayPlan.getAfternoonEvent());
                useEmptyEvent(currHolder.tvAfternoonName, currHolder.ivAfternoonImage, currHolder.tvAfternoonPrice);
                currHolder.cvAfternoon.setOnClickListener(null);
                currDayPlan.removeAfternoonEvent();
            }
        } else if (timeOfDay.contentEquals(eveningEvent)) {
            if (currDayPlan.getEveningEvent() != null) {
                allAvailableEvents.add(currDayPlan.getEveningEvent());
                useEmptyEvent(currHolder.tvEveningName, currHolder.ivEveningImage, currHolder.tvEveningPrice);
                currHolder.cvEvening.setOnClickListener(null);
                currDayPlan.removeEveningEvent();
            }
        }
    }

    private void generateEvent() {
        if (timeOfDay.contentEquals(morningEvent)) {

        } else if (timeOfDay.contentEquals(afternoonEvent)) {

        } else if (timeOfDay.contentEquals(eveningEvent)) {

        }
    }


    // Returns the total count of dayPlans
    @Override
    public int getItemCount() {
        return dayPlans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

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
        private TextView tvMorningPrice;
        private TextView tvAfternoonPrice;
        private TextView tvEveningPrice;

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
            tvMorningPrice = itemView.findViewById(R.id.tvMorningPrice);
            tvAfternoonPrice = itemView.findViewById(R.id.tvAfternoonPrice);
            tvEveningPrice = itemView.findViewById(R.id.tvEveningPrice);
        }
    }

    public void useEmptyEvent(TextView tvEventName, ImageView ivEventImage, TextView tvEventPrice) {
        tvEventName.setText(R.string.empty_slot);
        ivEventImage.setImageResource(R.drawable.emptyevent);
        tvEventPrice.setText("");
    }
}
