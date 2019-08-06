package com.codepath.travelapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travelapp.Activities.MainActivity;
import com.codepath.travelapp.Adapters.AchievementAdapter;
import com.codepath.travelapp.Models.Achievement;
import com.codepath.travelapp.Models.User;
import com.codepath.travelapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.parse.ParseUser.getCurrentUser;

public class AchievementFragment extends Fragment {
    private static final int NUM_COLUMNS = 3;
    private AchievementAdapter achievementAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
        return inflater.inflate(R.layout.fragment_achievement, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final RecyclerView rvAchievements = view.findViewById(R.id.rvAchievements);
        ArrayList<Achievement> allAchievements = new ArrayList<>();
        ArrayList<Achievement> earnedAchievements = new ArrayList<>();

        achievementAdapter = new AchievementAdapter(allAchievements, earnedAchievements);
        rvAchievements.setAdapter(achievementAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),
                GridLayoutManager.VERTICAL);
        gridLayoutManager.setSpanCount(NUM_COLUMNS);
        rvAchievements.setLayoutManager(gridLayoutManager);

        achievementAdapter.clearAllAchievements();
        final ParseQuery<Achievement> achievementQuery = new ParseQuery<>(Achievement.class);
        achievementQuery.findInBackground(new FindCallback<Achievement>() {
            @Override
            public void done(List<Achievement> achievements, ParseException e) {
                if (e == null) {
                    achievementAdapter.addAllAchievements(achievements);
                    achievementAdapter.clearEarnedAchievements();
                    User user = (User) getCurrentUser();

                    ParseQuery<Achievement> achievementParseQuery = user.getAchievementRelation()
                            .getQuery();
                    achievementParseQuery.findInBackground(new FindCallback<Achievement>() {
                        @Override
                        public void done(List<Achievement> objects, ParseException e) {
                            if(e == null) {
                                achievementAdapter.addEarnedAchievements(objects);
                            } else {
                                e.printStackTrace();
                                showAlertDialog();
                            }
                        }
                    });
                } else {
                    e.printStackTrace();
                    showAlertDialog();
                }
            }
        });

    }
    private void showAlertDialog() {
        AlertDialog dialog = new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                .setTitle("Error loading achievements.")
                .setPositiveButton("OK", null)
                .create();
        dialog.show();
    }

}
