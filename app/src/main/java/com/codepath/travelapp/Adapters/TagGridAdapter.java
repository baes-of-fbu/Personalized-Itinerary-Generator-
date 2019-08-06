package com.codepath.travelapp.Adapters;

import android.content.Context;
import android.graphics.Color;
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
import java.util.List;

public class TagGridAdapter extends RecyclerView.Adapter<TagGridAdapter.ViewHolder> {
    private Context context;
    private List<Tag> mTags;
    private ArrayList<Tag> selectedTags;

    public TagGridAdapter(List<Tag> tags) {
        mTags = tags;
    }

    @NonNull
    @Override
    public TagGridAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        selectedTags = new ArrayList<>();

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_tag, parent, false);
        return new ViewHolder(contactView);
    }


    @Override
    public void onBindViewHolder(@NonNull TagGridAdapter.ViewHolder viewHolder, int position) {
        Tag tag = mTags.get(position);
        viewHolder.tvTagName.setText(tag.getName());
        ParseFile image = tag.getImage();
        Glide.with(context)
                .load(image.getUrl())
                .into(viewHolder.ivTagImage);
    }

    @Override
    public int getItemCount() {
        return mTags.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivTagImage;
        private TextView tvTagName;

        ViewHolder(View itemView) {
            super(itemView);

            ivTagImage = itemView.findViewById(R.id.ivTagImage);
            tvTagName = itemView.findViewById(R.id.tvTagName);

            ivTagImage.setColorFilter(Color.argb(200, 200, 200, 200));
            itemView.setTag("grayed");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Tag tag = mTags.get(getLayoutPosition());

            if (view.getTag() != "grayed") {
                ivTagImage.setColorFilter(Color.argb(200, 200, 200, 200));
                view.setTag("grayed");
                selectedTags.remove(tag);
            } else {
                ivTagImage.setColorFilter(null);
                view.setTag("");
                selectedTags.add(tag);
            }
        }
    }

    public ArrayList<Tag> getSelectedTags() {
        // if NONE of the tags are selected, then all of the tags should be considered "selected"
        if (selectedTags.size() < 1) {
            selectedTags.addAll(mTags);
        }
        return selectedTags;
    }
}
