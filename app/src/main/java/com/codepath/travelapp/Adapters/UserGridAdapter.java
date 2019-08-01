package com.codepath.travelapp.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.travelapp.Activities.MainActivity;
import com.codepath.travelapp.Fragments.ProfileFragment;
import com.codepath.travelapp.Models.User;
import com.codepath.travelapp.R;

import java.util.ArrayList;
import java.util.List;

public class UserGridAdapter extends RecyclerView.Adapter<UserGridAdapter.ViewHolder> {

    private Context context;
    private List<User> users;
    private ArrayList<User> selectedUsers;

    public UserGridAdapter(ArrayList<User> users){ this.users = users;}

    @NonNull
    @Override
    public UserGridAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserGridAdapter.ViewHolder holder, int position) {
        User user = users.get(position);
        holder.tvBio.setText(user.getBio());
        holder.tvHomestate.setText(user.getHomeState());
        holder.tvFullName.setText(user.getUsername());
        Glide.with(context)
                .load(user.getProfileImage().getUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.ivImage);
   }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }
    public void addAll(List<User> list) {
        users.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView ivImage;
        private TextView tvFullName;
        private TextView tvBio;
        private TextView tvHomestate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvBio = itemView.findViewById(R.id.tvBio);
            tvHomestate = itemView.findViewById(R.id.tvHomeState);
            tvFullName = itemView.findViewById(R.id.tvUsername);


        }

        @Override
        public void onClick(View view) {
            final User user = users.get(getAdapterPosition());
            if (user != null) {
                Fragment fragment = new ProfileFragment();

                Bundle bundle = new Bundle();
                bundle.putParcelable("User", user);
                fragment.setArguments(bundle);

                MainActivity.fragmentManager.beginTransaction()
                        .replace(R.id.flContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

}
