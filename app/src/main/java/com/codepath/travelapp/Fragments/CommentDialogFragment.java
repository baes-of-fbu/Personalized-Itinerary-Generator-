package com.codepath.travelapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.travelapp.Adapters.CommentAdapter;
import com.codepath.travelapp.Models.Comment;
import com.codepath.travelapp.Models.Trip;
import com.codepath.travelapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.List;

public class CommentDialogFragment extends DialogFragment {

    private TextView tvTripName;
    private ImageView ivProfileImage;
    private EditText etNewComment;
    private RecyclerView rvComments;
    private Button btnPost;

    private CommentAdapter adapter;
    private Trip trip;

    public CommentDialogFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        trip = bundle.getParcelable("trip");
        return inflater.inflate(R.layout.fragment_comment_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTripName = view.findViewById(R.id.tvTripName);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        etNewComment = view.findViewById(R.id.etNewComment);
        rvComments = view.findViewById(R.id.rvComments);
        btnPost = view.findViewById(R.id.btnPost);

        tvTripName.setText(trip.getName());

        if (trip.getOwner().get("profileImage") != null) {
            ParseFile image = (ParseFile) trip.getOwner().get("profileImage");
            assert image != null;
            if (getContext() != null) {
                Glide.with(getContext())
                        .load(image.getUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(ivProfileImage);
            }
        }

        ArrayList<Comment> mComments = new ArrayList<>();
        adapter = new CommentAdapter(mComments);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvComments.setLayoutManager(linearLayoutManager);
        rvComments.setAdapter(adapter);

        trip.getComments().getQuery().findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> objects, ParseException e) {
                if (e == null) {
                    adapter.addAll(objects);
                } else {
                    e.printStackTrace();
                }
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Comment comment = new Comment();
                comment.setComment(etNewComment.getText().toString());
            }
        });
    }
}
