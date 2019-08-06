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
import com.codepath.travelapp.Models.Tag;
import com.codepath.travelapp.R;
import com.parse.ParseFile;

import java.util.ArrayList;

public class TagSelectedAdapter extends RecyclerView.Adapter<TagSelectedAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Tag> selectedTags;

    public TagSelectedAdapter(ArrayList<Tag> tags) {
        selectedTags = tags;
    }

    @NonNull
    @Override
    public TagSelectedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_tag, parent, false);
        return new ViewHolder(view);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull TagSelectedAdapter.ViewHolder viewHolder, int position) {
        Tag tag = selectedTags.get(position);
        viewHolder.tvTagName.setText(tag.getName());
        ParseFile image = tag.getImage();
        Glide.with(context)
                .load(image.getUrl())
                .into(viewHolder.ivTagImage);
    }

    @Override
    public int getItemCount() {
        return selectedTags.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivTagImage;
        private TextView tvTagName;

        ViewHolder(View itemView) {
            super(itemView);

            ivTagImage = itemView.findViewById(R.id.ivTagImage);
            tvTagName = itemView.findViewById(R.id.tvTagName);
        }
    }
}
