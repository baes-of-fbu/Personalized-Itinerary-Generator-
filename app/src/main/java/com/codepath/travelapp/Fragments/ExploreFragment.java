package com.codepath.travelapp.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.codepath.travelapp.Activities.MainActivity;
import com.codepath.travelapp.Adapters.ViewPagerAdapter;
import com.codepath.travelapp.R;
import com.google.android.material.tabs.TabLayout;

public class ExploreFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager viewPager = view.findViewById(R.id.viewPager);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        viewPager.setAdapter((new ViewPagerAdapter(getChildFragmentManager())));
        tabLayout.setupWithViewPager(viewPager);
    }
    @Override
    public void onAttach(@NonNull Activity activity) {
        FragmentActivity myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }
}
