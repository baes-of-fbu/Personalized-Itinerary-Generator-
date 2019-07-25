package com.codepath.travelapp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.travelapp.Models.Tag;
import com.codepath.travelapp.R;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.List;

public class TagSelectedAdapter extends RecyclerView.Adapter<TagSelectedAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Tag> selectedTags;

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivTagImage;
        private TextView tvTagName;

        ViewHolder(View itemView) {
            super(itemView);

            ivTagImage = itemView.findViewById(R.id.ivTagImage);
            tvTagName = itemView.findViewById(R.id.tvTagName);
        }
    }

    // Pass in the contact array into the constructor
    public TagSelectedAdapter(ArrayList<Tag> tags) {
        selectedTags = tags;
    }

    @Override
    public TagSelectedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_tag, parent, false);

        return new ViewHolder(view);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(TagSelectedAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Tag tag = selectedTags.get(position);
        // Set item views based on your views and data model
        viewHolder.tvTagName.setText(tag.getName());
        ParseFile image = tag.getImage();
        Glide.with(context)
                .load(image.getUrl())
                .into(viewHolder.ivTagImage);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return selectedTags.size();
    }
}
