package com.codepath.instagram.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.instagram.R;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramComment;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by koulmomo on 12/1/15.
 */
public class InstagramCommentsAdapter extends RecyclerView.Adapter<InstagramCommentsAdapter.CommentItemHolder> {

    public static class CommentItemHolder extends RecyclerView.ViewHolder {
        ImageView mCommentAvatarImageView;

        TextView mCommentTextView;
        TextView mCommentTimestampTextView;

        public CommentItemHolder(View itemView) {
            super(itemView);

            mCommentAvatarImageView = (ImageView) itemView.findViewById(R.id.ivCommentAvatar);
            mCommentTextView = (TextView) itemView.findViewById(R.id.tvComment);
            mCommentTimestampTextView = (TextView) itemView.findViewById(R.id.tvCommentTimestamp);
        }
    }

    private List<InstagramComment> mComments;

    public InstagramCommentsAdapter(List<InstagramComment> comments) {
        mComments = comments;
    }

    @Override
    public CommentItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new InstagramCommentsAdapter.CommentItemHolder(
                LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_comment, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(CommentItemHolder holder, int position) {
        InstagramComment comment = mComments.get(position);

        renderAvatar(holder, comment);
        renderComment(holder, comment);
        renderCommentTimestamp(holder, comment);
    }

    private void renderAvatar(CommentItemHolder holder, InstagramComment comment) {
        Picasso.with(holder.mCommentAvatarImageView.getContext())
                .load(comment.user.profilePictureUrl)
                .fit()
                .into(holder.mCommentAvatarImageView);
    }

    private void renderComment(CommentItemHolder holder, InstagramComment comment) {
        holder.mCommentTextView.setText(
                InstagramPostsAdapter.prependWithBlueUsername(
                        holder.mCommentTextView.getContext(),
                        comment.user.userName,
                        comment.text
                ));
    }

    private void renderCommentTimestamp(CommentItemHolder holder, InstagramComment comment) {
        holder.mCommentTimestampTextView.setText(
                DateUtils.getRelativeTimeSpanString(
                        comment.createdTime * DateUtils.SECOND_IN_MILLIS,
                        System.currentTimeMillis(),
                        DateUtils.SECOND_IN_MILLIS
        ).toString());
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }
}