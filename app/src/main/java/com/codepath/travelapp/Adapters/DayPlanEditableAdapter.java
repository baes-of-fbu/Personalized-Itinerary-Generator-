package com.codepath.travelapp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
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
import java.util.List;
import java.util.Random;

public class DayPlanEditableAdapter extends RecyclerView.Adapter<DayPlanEditableAdapter.ViewHolder> implements EditEventDialogFragment.Listener {

    private Context context;
    private DayPlan currDayPlan;
    private ArrayList<DayPlan> dayPlans;
    private ArrayList<Event> allAvailableEvents;
    private ViewHolder currHolder;
    private TextView tvTripCost;
    private TextView tvTripRemainingMoney;

    private String timeOfDay;
    private String morningEvent;
    private String afternoonEvent;
    private String eveningEvent;
    private String removeEvent;
    private String reGenerateEvent;
    private int budget;
    private int remainingMoney;

    public DayPlanEditableAdapter(ArrayList<DayPlan> dayPlans, ArrayList<Event> allAvailableEvents, int budget, int remainingMoney, TextView tvTripCost, TextView tvTripRemainingMoney) {
        this.dayPlans = dayPlans;
        this.allAvailableEvents = allAvailableEvents;
        this.budget = budget;
        this.remainingMoney = remainingMoney;
        this.tvTripCost = tvTripCost;
        this.tvTripRemainingMoney = tvTripRemainingMoney;
    }

    @NonNull
    @Override
    public DayPlanEditableAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        morningEvent = context.getResources().getString(R.string.morning_event);
        afternoonEvent = context.getResources().getString(R.string.afternoon_event);
        eveningEvent = context.getResources().getString(R.string.evening_event);
        removeEvent = context.getResources().getString(R.string.remove_event);
        reGenerateEvent = context.getResources().getString(R.string.regenerate_event);

        View view = LayoutInflater.from(context).inflate(R.layout.item_editable_day_plan, parent, false);
        return new DayPlanEditableAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayPlanEditableAdapter.ViewHolder holder, int position) {
        // Get the current day
        final DayPlan dayPlan = dayPlans.get(position);
        holder.tvDayTitle.setText(dayPlan.getDate().toString().substring(0,10));

        if (dayPlan.getMorningEvent() == null) {
            // Fills morning card view with empty event and removes onClickListener
            useEmptyEvent(holder.tvMorningName, holder.ivMorningImage, holder.tvMorningPrice);
            holder.cvMorning.setOnClickListener(null);
        } else {
            // Try/catch needed for .fetchIfNeeded()
            try {
                holder.tvMorningName.setText(dayPlan.getMorningEvent().fetchIfNeeded().getString(Event.KEY_NAME));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // Populates view with morning event cost
            int cost = (int) dayPlan.getMorningEvent().getCost();
            if (cost == 0) {
                holder.tvMorningPrice.setText(context.getResources().getString(R.string.free));
                holder.tvMorningPrice.setTextColor(context.getResources().getColor(R.color.LightSkyBlue));
            } else {
                holder.tvMorningPrice.setText(String.format("$%s", String.valueOf(cost)));
                holder.tvMorningPrice.setTextColor(context.getResources().getColor(R.color.black));
            }

            // Populated view with morning event image
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
            holder.cvAfternoon.setOnClickListener(null);
        } else {
            // Try/catch needed for .fetchIfNeeded()
            try {
                holder.tvAfternoonName.setText(dayPlan.getAfternoonEvent().fetchIfNeeded().getString(Event.KEY_NAME));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // Populates view with afternoon event cost
            int cost = (int) dayPlan.getAfternoonEvent().getCost();
            if (cost == 0) {
                holder.tvAfternoonPrice.setText(context.getResources().getString(R.string.free));
                holder.tvAfternoonPrice.setTextColor(context.getResources().getColor(R.color.LightSkyBlue));
            } else {
                holder.tvAfternoonPrice.setText(String.format("$%s", String.valueOf(cost)));
                holder.tvAfternoonPrice.setTextColor(context.getResources().getColor(R.color.black));
            }
            // Populates view with afternoon event image
            Glide.with(context)
                    .load(dayPlan.getAfternoonEvent().getImage().getUrl())
                    .into(holder.ivAfternoonImage);
            // Populates view with listener to afternoon card view
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
            holder.cvEvening.setOnClickListener(null);
        } else {
            // Try/catch needed for .fetchIfNeeded()
            try {
                holder.tvEveningName.setText(dayPlan.getEveningEvent().fetchIfNeeded().getString(Event.KEY_NAME));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // Populates view with evening event cost
            int cost = (int) dayPlan.getEveningEvent().getCost();
            if (cost == 0) {
                holder.tvEveningPrice.setText(context.getResources().getString(R.string.free));
                holder.tvEveningPrice.setTextColor(context.getResources().getColor(R.color.LightSkyBlue));
            } else {
                holder.tvEveningPrice.setText(String.format("$%s", String.valueOf(cost)));
                holder.tvEveningPrice.setTextColor(context.getResources().getColor(R.color.black));
            }
            // Populates view with evening event image
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
                editEventListeners(holder, dayPlan, morningEvent);
            }
        });

        holder.ivEditAfternoonEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editEventListeners(holder, dayPlan, afternoonEvent);
            }
        });

        holder.ivEditEveningEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editEventListeners(holder, dayPlan, eveningEvent);
            }
        });
    }

    private void editEventListeners(ViewHolder holder, DayPlan dayPlan, String eventTime) {
        currHolder = holder;
        currDayPlan = dayPlan;
        timeOfDay = eventTime;
        promptUser();
    }

    // Prompts user with options for editing an event
    private void promptUser() {
        FragmentManager fragmentManager = MainActivity.fragmentManager;
        EditEventDialogFragment editEventDialogFragment = EditEventDialogFragment.newInstance();
        editEventDialogFragment.setListener(DayPlanEditableAdapter.this);
        editEventDialogFragment.show(fragmentManager, "fragment_edit_event_options");
    }

    // This is called when the dialog is closed or the user has selected on option
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void returnData(String action) {
        if (action.contentEquals(removeEvent)) {
            // Removes event from the DayPlan
            removeEvent();
            updateTripCost();
        } else if (action.contentEquals(reGenerateEvent)) {
            // Removes event and regenerates schedule
            generateEvent();
            updateTripCost();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void removeEvent() {
        if (timeOfDay.contentEquals(morningEvent)) {
            if (currDayPlan.getMorningEvent() != null) {
                allAvailableEvents.add(currDayPlan.getMorningEvent());
                remainingMoney = Math.addExact(remainingMoney, (int) currDayPlan.getMorningEvent().getCost());
                useEmptyEvent(currHolder.tvMorningName, currHolder.ivMorningImage, currHolder.tvMorningPrice);
                currHolder.cvMorning.setOnClickListener(null);
                currDayPlan.removeMorningEvent();
            }
        } else if (timeOfDay.contentEquals(afternoonEvent)) {
            if (currDayPlan.getAfternoonEvent() != null) {
                allAvailableEvents.add(currDayPlan.getAfternoonEvent());
                remainingMoney = Math.addExact(remainingMoney, (int) currDayPlan.getAfternoonEvent().getCost());
                useEmptyEvent(currHolder.tvAfternoonName, currHolder.ivAfternoonImage, currHolder.tvAfternoonPrice);
                currHolder.cvAfternoon.setOnClickListener(null);
                currDayPlan.removeAfternoonEvent();
            }
        } else if (timeOfDay.contentEquals(eveningEvent)) {
            if (currDayPlan.getEveningEvent() != null) {
                allAvailableEvents.add(currDayPlan.getEveningEvent());
                remainingMoney = Math.addExact(remainingMoney, (int) currDayPlan.getEveningEvent().getCost());
                useEmptyEvent(currHolder.tvEveningName, currHolder.ivEveningImage, currHolder.tvEveningPrice);
                currHolder.cvEvening.setOnClickListener(null);
                currDayPlan.removeEveningEvent();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void generateEvent() {
        // shuffleEvents() clears the allAvailableEvents array
        // and returns a new shuffled list of events
        allAvailableEvents.addAll(shuffleEvents());
        Event event = getEvent();
        if (event != null) {
            allAvailableEvents.remove(event);
            remainingMoney = Math.subtractExact(remainingMoney, (int) event.getCost());
            if (timeOfDay.contentEquals(morningEvent)) {
                if (currDayPlan.getMorningEvent() != null) {
                    allAvailableEvents.add(currDayPlan.getMorningEvent());
                    remainingMoney = Math.addExact(remainingMoney, (int) currDayPlan.getMorningEvent().getCost());
                    currDayPlan.removeMorningEvent();
                }
                currDayPlan.setMorningEvent(event);

                notifyDataSetChanged();
            } else if (timeOfDay.contentEquals(afternoonEvent)) {
                if (currDayPlan.getAfternoonEvent() != null) {
                    allAvailableEvents.add(currDayPlan.getAfternoonEvent());
                    remainingMoney = Math.addExact(remainingMoney, (int) currDayPlan.getAfternoonEvent().getCost());
                    currDayPlan.removeAfternoonEvent();
                }
                currDayPlan.setAfternoonEvent(event);
                notifyDataSetChanged();
            } else if (timeOfDay.contentEquals(eveningEvent)) {
                if (currDayPlan.getEveningEvent() != null) {
                    allAvailableEvents.add(currDayPlan.getEveningEvent());
                    remainingMoney = Math.addExact(remainingMoney, (int) currDayPlan.getEveningEvent().getCost());
                    currDayPlan.removeEveningEvent();
                }
                currDayPlan.setEveningEvent(event);
                notifyDataSetChanged();
            }
        } else {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("No available events for this budget. Consider increasing budget")
                    .setPositiveButton("OK", null)
                    .create();
            dialog.show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateTripCost() {
        int tripCost = Math.subtractExact(budget, remainingMoney);
        tvTripCost.setText(String.format("$%s", String.valueOf(tripCost)));
        tvTripRemainingMoney.setText(String.format("$%s", String.valueOf(remainingMoney)));
        if (remainingMoney < 0) {
            tvTripRemainingMoney.setTextColor(Color.RED);
        } else if (remainingMoney > 0) {
            tvTripRemainingMoney.setTextColor(context.getResources().getColor(R.color.LightSkyBlue)); // TODO change color?
        }
    }

    private Event getEvent() {
        int numEvents = allAvailableEvents.size();
        // Loops through each event
        for (int i = 0; i < numEvents; i++) {
            Event event = allAvailableEvents.get(i);
            if (isEventAvailable(event) && isEventWithinBudget(event)) {
                // Returns event if it is available and within budget
                return event;
            }
        }
        // Returns null if no event was found
        return null;
    }

    // Events are shuffled so that each trip that is made can have a unique order of events
    private ArrayList<Event> shuffleEvents() {
        int numEvents = allAvailableEvents.size();
        ArrayList<Event> shuffledEvents = new ArrayList<>();
        // Loops through every event
        for (int i = 0; i < numEvents; i++) {
            // Selects a random event
            Event event = getRandomElement(allAvailableEvents);
            shuffledEvents.add(event);
            allAvailableEvents.remove(event);
        }
        // Returns a new shuffled list of events
        return shuffledEvents;
    }

    // Returns a random event from a list of events
    private Event getRandomElement(List<Event> list) {
        Random rand = new Random();
        if (list.size() == 0) {
            return null;
        }
        return list.get(rand.nextInt(list.size()));
    }

    // Returns if the event is available during a given time of day
    private Boolean isEventAvailable(Event event) {
        if (timeOfDay.contentEquals(morningEvent)) {
            return event.isAvailableMorning();
        } else if (timeOfDay.contentEquals(afternoonEvent)) {
            return event.isAvailableAfternoon();
        } else {
            return event.isAvailableEvening();
        }
    }

    // Checks if the cost of an event is within the budget
    private Boolean isEventWithinBudget(Event event) {
        return (int) event.getCost() <= remainingMoney;
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

    private void useEmptyEvent(TextView tvEventName, ImageView ivEventImage, TextView tvEventPrice) {
        tvEventName.setText(R.string.empty_slot);
        ivEventImage.setImageResource(R.drawable.emptyevent);
        tvEventPrice.setText("");
    }
}
