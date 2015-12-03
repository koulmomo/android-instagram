package com.codepath.instagram.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.instagram.R;
import com.codepath.instagram.models.InstagramUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by koulmomo on 12/2/15.
 */
public class SearchUserResultsAdapter extends RecyclerView.Adapter<SearchUserResultsAdapter.UserItemHolder> {

    public class UserItemHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tvUserNameSearchResult)
        TextView mUserNameTV;

        @Bind(R.id.ivProfilePicSearchResult) ImageView mProfilePicIV;

        @Bind(R.id.tvFullName) TextView mFullNameTV;

        public UserItemHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    private List<InstagramUser> mUserSearchResults;
    private Activity mParentActivity;

    public SearchUserResultsAdapter(Activity parent, List<InstagramUser> users) {
        mUserSearchResults = users;
        mParentActivity = parent;
    }

    @Override
    public UserItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SearchUserResultsAdapter.UserItemHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_item_user, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(UserItemHolder holder, int position) {
        InstagramUser user = mUserSearchResults.get(position);
        renderProfilePic(holder, user);
        renderUserName(holder, user);
        renderFullName(holder, user);
    }

    private void renderFullName(UserItemHolder holder, InstagramUser user) {
        holder.mFullNameTV.setText(user.fullName);
    }

    private void renderProfilePic(UserItemHolder holder, InstagramUser user) {
        Picasso.with(holder.mProfilePicIV.getContext())
                .load(user.profilePictureUrl)
                .fit()
                .placeholder(R.drawable.gray_oval)
                .into(holder.mProfilePicIV);
    }

    private void renderUserName(UserItemHolder holder, InstagramUser user) {
        holder.mUserNameTV.setText(user.userName);
    }

    @Override
    public int getItemCount() {
        return mUserSearchResults.size();
    }
}
