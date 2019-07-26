package com.codepath.travelapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.travelapp.Models.Event;
import com.codepath.travelapp.R;

import java.util.ArrayList;

public class EditableEventAdapter extends ArrayAdapter<Event> {
    public EditableEventAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Event event = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_editable_event, parent, false);
        }
        // Lookup view for data population
        TextView tvEventName = convertView.findViewById(R.id.tvEventName);
        TextView tvEventPrice = convertView.findViewById(R.id.tvEventPrice);
        ImageView ivEventImage = convertView.findViewById(R.id.ivEventImage);
        ImageView ivClose = convertView.findViewById(R.id.ivEdit);
        // Populate the data into the template view using the data object
        tvEventName.setText(event.getName());
        tvEventPrice.setText(String.valueOf(event.getCost()));
        Glide.with(convertView)
                .load(event.getImage().getUrl())
                .into(ivEventImage);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Event", "Clicked on close button");
            }
        });
        return convertView;
    }
}
