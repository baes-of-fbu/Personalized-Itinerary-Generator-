package com.codepath.travelapp.Fragments;

import android.app.ProgressDialog;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.codepath.travelapp.Models.User;
import com.codepath.travelapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class CommentDialogFragment extends DialogFragment {

    private EditText etNewComment;

    private CommentAdapter adapter;
    private Trip trip;

    public CommentDialogFragment() {
    }

    public static CommentDialogFragment newInstance(Trip trip) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("trip", trip);
        CommentDialogFragment fragment = new CommentDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            trip = bundle.getParcelable("trip");
        }
        return inflater.inflate(R.layout.fragment_comment_dialog, container, false);
    }

    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((size.x), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvTripName = view.findViewById(R.id.tvTripName);
        ImageView ivProfileImage = view.findViewById(R.id.ivProfileImage);
        etNewComment = view.findViewById(R.id.etNewComment);
        RecyclerView rvComments = view.findViewById(R.id.rvComments);
        Button btnPost = view.findViewById(R.id.btnPost);

        tvTripName.setText(trip.getName());

        if (ParseUser.getCurrentUser().get("profileImage") != null) {
            ParseFile image = (ParseFile) ParseUser.getCurrentUser().get("profileImage");
            assert image != null;
            if (getContext() != null) {
                Glide.with(getContext())
                        .load(image.getUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(ivProfileImage);
            }
        }

        final ArrayList<Comment> mComments = new ArrayList<>();
        adapter = new CommentAdapter(mComments);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvComments.setLayoutManager(linearLayoutManager);
        rvComments.setAdapter(adapter);

        populateComments();

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = etNewComment.getText().toString();
                if (text.length() > 0) {
                    final Comment comment = new Comment();
                    comment.setComment(text);
                    comment.setUser((User) ParseUser.getCurrentUser());
                    comment.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                trip.getComments().add(comment);
                                trip.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        populateComments();
                                    }
                                });
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                    etNewComment.setText("");
                    populateComments();
                } else {
                    Toast.makeText(getContext(), "Please input a comment", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private void populateComments() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Adding your comment...");
        progressDialog.setTitle("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
        trip.getComments().getQuery().orderByAscending("createdAt")
                .findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> objects, ParseException e) {
                if (e == null) {
                    adapter.clear();
                    adapter.addAll(objects);
                    progressDialog.hide();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
