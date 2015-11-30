package com.codepath.instagram.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.instagram.R;
import com.codepath.instagram.models.InstagramPost;

import java.util.List;

/**
 * Created by koulmomo on 11/30/15.
 */
public class InstagramPostsAdapter extends RecyclerView.Adapter<InstagramPostsAdapter.PostItemViewHolder> {
    private List<InstagramPost> instagramPosts;

    public static class PostItemViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;

        public PostItemViewHolder(View itemView) {
            super(itemView);

            userNameTextView = (TextView) itemView.findViewById(R.id.tvUserName);
        }
    }

    public InstagramPostsAdapter(List<InstagramPost> instagramPosts) {
        this.instagramPosts = instagramPosts;
    }

    @Override
    public InstagramPostsAdapter.PostItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View postView = inflater.inflate(R.layout.layout_item_post, parent, false);

        return new InstagramPostsAdapter.PostItemViewHolder(postView);
    }

    @Override
    public void onBindViewHolder(PostItemViewHolder holder, int position) {
        TextView userNameTextView = holder.userNameTextView;
        userNameTextView.setText(instagramPosts.get(position).user.userName);
    }

    @Override
    public int getItemCount() {
        return instagramPosts.size();
    }
}
