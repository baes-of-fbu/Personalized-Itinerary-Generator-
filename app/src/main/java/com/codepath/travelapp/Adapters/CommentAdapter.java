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
import com.bumptech.glide.request.RequestOptions;
import com.codepath.travelapp.Models.Comment;
import com.codepath.travelapp.R;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Comment> comments;

    public CommentAdapter(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);

        holder.tvComment.setText(comment.getComment());

        try {
            if (comment.getUser().fetchIfNeeded().get("profileImage") != null) {
                ParseFile image = (ParseFile) comment.getUser().get("profileImage");
                assert image != null;
                Glide.with(context)
                        .load(image.getUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(holder.ivProfileImage);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvComment;
        private ImageView ivProfileImage;

        ViewHolder(View itemView) {
            super(itemView);
            tvComment = itemView.findViewById(R.id.tvComment);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
        }
    }

    public void clear() {
        comments.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Comment> list) {
        comments.addAll(list);
        notifyDataSetChanged();
    }
}
