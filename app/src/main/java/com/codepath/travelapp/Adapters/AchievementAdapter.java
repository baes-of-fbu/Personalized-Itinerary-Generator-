package com.codepath.travelapp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.travelapp.Activities.MainActivity;
import com.codepath.travelapp.Fragments.AchievementDialogFragment;
import com.codepath.travelapp.Models.Achievement;
import com.codepath.travelapp.R;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.List;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Achievement> allAchievements;
    private ArrayList<Achievement> earnedAchievements;


    public AchievementAdapter(ArrayList<Achievement> allAchievements, ArrayList<Achievement> earnedAchievements) {
        this.allAchievements = allAchievements;
        this.earnedAchievements = earnedAchievements;
    }

    @NonNull
    @Override
    public AchievementAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_achievment, parent, false);
        return new ViewHolder(view);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull AchievementAdapter.ViewHolder holder, int position) {
        // Get an achievement
        Achievement achievement = allAchievements.get(position);

        holder.tvAchievement.setText(achievement.getName());

        if (achievement.getImage() != null) {
            ParseFile image = achievement.getImage();
            assert image != null;
            Glide.with(context)
                    .load(image.getUrl())
                    .into(holder.ivAchievement);
        }
        if (isNotEarnedAchievement(achievement)) {
            holder.ivAchievement.setColorFilter(Color.argb(250, 200, 200, 200));
        }
        else {
            holder.ivAchievement.setColorFilter(Color.argb(0,0,0,0));
        }
    }

    @Override
    public int getItemCount() { return allAchievements.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvAchievement;
        private ImageView ivAchievement;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //Find views that will be populated
            tvAchievement = itemView.findViewById(R.id.tvAchievement);
            ivAchievement = itemView.findViewById(R.id.ivAchievement);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Achievement achievement = allAchievements.get(getAdapterPosition());
            FragmentManager fragmentManager = MainActivity.fragmentManager;
            AchievementDialogFragment achievementDialougeFragment = AchievementDialogFragment.newInstance(achievement);
            achievementDialougeFragment.show(fragmentManager, "fragment_edit_trip_options");
        }
    }
    public void clearAllAchievements() {
        allAchievements.clear();
        notifyDataSetChanged();
    }
    public void clearEarnedAchievements() {
        earnedAchievements.clear();
        notifyDataSetChanged();
    }
    public void addAllAchievements(List<Achievement> list) {
        allAchievements.addAll(list);
        notifyDataSetChanged();
    }
    public void addEarnedAchievements(List<Achievement> list) {
        earnedAchievements.addAll(list);
        notifyDataSetChanged();
    }
    private boolean isNotEarnedAchievement (Achievement achievement) {
        String achievementName = achievement.getName();
        for (int i = 0; i < earnedAchievements.size(); i++) {
            Achievement currentAchievement = earnedAchievements.get(i);
            String currentName = currentAchievement.getName();
            if (currentName.contains(achievementName)) {
                return false;
            }
        }
        return true;
    }

}
