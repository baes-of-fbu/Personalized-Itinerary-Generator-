package com.codepath.travelapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travelapp.Adapters.EventAdapter;
import com.codepath.travelapp.Adapters.UserAdapter;
import com.codepath.travelapp.Models.Event;
import com.codepath.travelapp.Models.User;
import com.codepath.travelapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class EventExploreFragment extends Fragment {

    protected EventAdapter eventsAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView rvEvents = view.findViewById(R.id.rvEvents);
        SearchView svEvents = view.findViewById(R.id.svEvents);
        ArrayList<Event> mEvents = new ArrayList<>();
        eventsAdapter = new EventAdapter(mEvents);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvEvents.setLayoutManager(linearLayoutManager);
        rvEvents.setAdapter(eventsAdapter);

        queryEvents(null);
        svEvents.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String name) {
                queryEvents(name);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String name) {
                queryEvents(name);
                return true;
            }
        });

    }

    private void queryEvents(String name) {
        eventsAdapter.clear();
        ParseQuery<Event> eventQuery = new ParseQuery<>(Event.class);
        if( name != null) {
            eventQuery.whereContains("name", name);
        }
        eventQuery.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e == null) {
                    eventsAdapter.addAll(events);
                }else{
                    e.printStackTrace();
                }
            }
        });
    }
}
