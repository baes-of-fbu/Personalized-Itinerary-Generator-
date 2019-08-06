package com.codepath.travelapp.Fragments;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.codepath.travelapp.Models.Achievement;
import com.codepath.travelapp.R;

import java.util.Objects;

public class AchievementDialogFragment extends DialogFragment {
    public AchievementDialogFragment() {
    }

    public static AchievementDialogFragment newInstance(Achievement achievement) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("achievement", achievement);
        AchievementDialogFragment frag = new AchievementDialogFragment();
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_achievement_dialouge, container);
    }

    public void onResume() {
        // Store access variables for window and blank point
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        Point size = new Point();
        if (window != null) {
            Display display = window.getWindowManager().getDefaultDisplay();
            display.getSize(size);
            // Set the width of the dialog proportional to 75% of the screen width
            window.setLayout((int) (size.x * 0.6), WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            super.onResume();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            final Achievement achievement = (Achievement) bundle.getSerializable("achievement");
            TextView tvTitle = view.findViewById(R.id.tvTitle);
            TextView tvDescription = view.findViewById(R.id.tvDescription);
            if (achievement != null) {
                tvTitle.setText(achievement.getName());
                tvDescription.setText(achievement.getDescription());
            }
        }
    }
}
