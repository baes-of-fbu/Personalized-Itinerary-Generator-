package com.codepath.travelapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travelapp.Activities.MainActivity;
import com.codepath.travelapp.Adapters.AchievementAdapter;
import com.codepath.travelapp.Models.Achievement;
import com.codepath.travelapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class AchievementFragment extends Fragment {
    private static final int NUM_COLUMNS = 3;
    private AchievementAdapter achievementAdapter;
    private ArrayList<Achievement> achievements;
    private ArrayList<Achievement> earnedAchievements;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
        return inflater.inflate(R.layout.fragment_achievement, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final RecyclerView rvAchievements = view.findViewById(R.id.rvAchievements);
        ArrayList<Achievement> achievements = new ArrayList<>();
        achievementAdapter = new AchievementAdapter(achievements);
        rvAchievements.setAdapter(achievementAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), GridLayoutManager.VERTICAL);
        gridLayoutManager.setSpanCount(NUM_COLUMNS);
        rvAchievements.setLayoutManager(gridLayoutManager);
        achievementAdapter.clear();
        final ParseQuery<Achievement> achievementquery = new ParseQuery<>(Achievement.class);
        achievementquery.findInBackground(new FindCallback<Achievement>() {
            @Override
            public void done(List<Achievement> achievements, ParseException e) {
                if (e == null) {
                    Log.d("DEBUG", "Successful query for achievements");
                    achievementAdapter.addAll(achievements);

                }else {
                    e.printStackTrace();
                    Log.d("DEBUG", "Error Loading achievements");
                }
            }
        });

    }

}
