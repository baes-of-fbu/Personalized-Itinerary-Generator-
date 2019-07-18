package com.codepath.travelapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import com.codepath.travelapp.Models.Tag;
import com.codepath.travelapp.R;

import java.util.List;

public class TagGridAdapter extends RecyclerView.Adapter<TagGridAdapter.ViewHolder> {

    private List<Tag> mTags; // Stores all of the tags

    public class ViewHolder extends RecyclerView.ViewHolder {

        public Button tagBtn;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            tagBtn = (Button) itemView.findViewById(R.id.tagBtn);
        }
    }

    // Pass in the contact array into the constructor
    public TagGridAdapter(List<Tag> tags) {
        mTags = tags;
    }

    @Override
    public TagGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_tag, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(TagGridAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Tag tag = mTags.get(position);

        // Set item views based on your views and data model
        Button button = viewHolder.tagBtn;
        button.setText(tag.getName());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mTags.size();
    }
}
