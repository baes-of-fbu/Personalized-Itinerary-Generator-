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
import com.codepath.travelapp.Models.Achievement;
import com.codepath.travelapp.R;
import com.parse.ParseFile;

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

    @Override
    public void onBindViewHolder(@NonNull AchievementAdapter.ViewHolder holder, int position) {
        // Get an achievement
        final Achievement achievement = achievements.get(position);
        holder.tvAchievment.setText(achievement.getName());

        if (achievement.getImage() != null) {
            ParseFile image = achievement.getImage();
            assert image != null;
            Glide.with(context)
                    .load(image.getUrl())
                    .into(holder.ivAchievment);
        }

    }

    @Override
    public int getItemCount() { return achievements.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAchievment;
        private ImageView ivAchievment;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //Find views that will be populated
            tvAchievment = itemView.findViewById(R.id.tvAchievement);
            ivAchievment = itemView.findViewById(R.id.ivAchievement);
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