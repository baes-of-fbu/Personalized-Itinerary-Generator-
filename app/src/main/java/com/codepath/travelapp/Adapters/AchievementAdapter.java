package com.codepath.travelapp.Adapters;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.travelapp.Activities.MainActivity;
import com.codepath.travelapp.Fragments.AchievementDialougeFragment;
import com.codepath.travelapp.Fragments.EditTripDialogFragment;
import com.codepath.travelapp.Fragments.LogoutDialogFragment;
import com.codepath.travelapp.Fragments.ProfileFragment;
import com.codepath.travelapp.Fragments.TripReviewFragment;
import com.codepath.travelapp.Models.Achievement;
import com.codepath.travelapp.Models.Trip;
import com.codepath.travelapp.R;
import com.parse.ParseFile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.ViewHolder> {
    private Context context;
    ArrayList<Achievement> achievements;


    public AchievementAdapter(ArrayList<Achievement> achievements) { this.achievements = achievements;}

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
        Achievement achievement = achievements.get(position);

        holder.tvAchievement.setText(achievement.getName());

        if (achievement.getImage() != null) {
            ParseFile image = achievement.getImage();
            assert image != null;
            Glide.with(context)
                    .load(image.getUrl())
                    .into(holder.ivAchievement);
        }

    }

    @Override
    public int getItemCount() { return achievements.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAchievement;
        private ImageView ivAchievement;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //Find views that will be populated
            tvAchievement = itemView.findViewById(R.id.tvAchievement);
            ivAchievement = itemView.findViewById(R.id.ivAchievement);
            ivAchievement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Achievement achievement = achievements.get(getAdapterPosition());
                    FragmentManager fragmentManager = MainActivity.fragmentManager;
                    AchievementDialougeFragment achievementDialougeFragment = AchievementDialougeFragment.newInstance(achievement);
                    achievementDialougeFragment.show(fragmentManager, "fragment_edit_trip_options");

                    }
            });
        }
    }
    public void clear() {
        achievements.clear();
        notifyDataSetChanged();
    }
    public void addAll(List<Achievement> list) {
        achievements.addAll(list);
        notifyDataSetChanged();
    }

}
