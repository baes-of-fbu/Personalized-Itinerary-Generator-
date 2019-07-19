package com.codepath.travelapp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.travelapp.Models.Tag;
import com.codepath.travelapp.R;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.List;

public class TagGridAdapter extends RecyclerView.Adapter<TagGridAdapter.ViewHolder> {

    private Context context;
    private List<Tag> mTags; // Stores all of the tags
    private List<Tag> selectedTags;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivTagImage;
        private TextView tvTagName;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            ivTagImage = (ImageView) itemView.findViewById(R.id.ivTagImage);
            tvTagName = (TextView) itemView.findViewById(R.id.tvTagName);

            itemView.setOnClickListener((View.OnClickListener) this);
        }

        @Override
        public void onClick(View view) {
            Tag tag = mTags.get(getLayoutPosition());

            if(view.getTag() != "grayed") {
                ivTagImage.setColorFilter(Color.argb(200,200,200,200));
                view.setTag("grayed");
                selectedTags.add(tag);
            } else {
                ivTagImage.setColorFilter(null);
                view.setTag("");
                selectedTags.remove(tag);
            }
        }
    }

    // Pass in the contact array into the constructor
    public TagGridAdapter(List<Tag> tags) {
        mTags = tags;
    }

    @Override
    public TagGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        selectedTags = new ArrayList<>();

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
        viewHolder.tvTagName.setText(tag.getName());
        ParseFile image = (ParseFile) tag.getImage();
        Glide.with(context)
                .load(image.getUrl())
                .into(viewHolder.ivTagImage);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mTags.size();
    }

    public List<Tag> getSelectedTags() {
        return selectedTags;
    }
}
