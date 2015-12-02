package com.codepath.instagram.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.codepath.instagram.R;
import com.codepath.instagram.activities.CommentsActivity;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramComment;
import com.codepath.instagram.models.InstagramPost;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by koulmomo on 11/30/15.
 */
public class InstagramPostsAdapter extends RecyclerView.Adapter<InstagramPostsAdapter.PostItemViewHolder> {

    public static class PostItemViewHolder extends RecyclerView.ViewHolder {
        ImageView mAvatarImageView;
        TextView mUserNameTextView;
        TextView mTimestampTextView;

        ImageView mPostPicImageView;

        TextView mLikesTextView;

        TextView mCaptionTextView;

        TextView mViewAllCommentsTextView;

        ImageView mSharePostImageView;

        LinearLayout mCommentsLinearLayout;

        public PostItemViewHolder(View itemView) {
            super(itemView);

            mAvatarImageView = (ImageView) itemView.findViewById(R.id.ivAvatar);
            mUserNameTextView = (TextView) itemView.findViewById(R.id.tvUserName);
            mTimestampTextView = (TextView) itemView.findViewById(R.id.tvTimestamp);

            mPostPicImageView = (ImageView) itemView.findViewById(R.id.ivPostPic);

            mLikesTextView = (TextView) itemView.findViewById(R.id.tvLikes);
            mCaptionTextView = (TextView) itemView.findViewById(R.id.tvCaption);
            mViewAllCommentsTextView = (TextView) itemView.findViewById(R.id.tvViewAllComments);
            mCommentsLinearLayout = (LinearLayout) itemView.findViewById(R.id.llComments);

            mSharePostImageView = (ImageView) itemView.findViewById(R.id.ivSharePost);
        }
    }

    protected List<InstagramPost> instagramPosts;
    private final static String VIEW_ALL_COMMENTS_TEMPLATE = "View all %d comments";

    private final static int COMMENTS_THRESHOLD = 2;

    private Activity mParentActivity;

    public InstagramPostsAdapter(Activity parent, List<InstagramPost> instagramPosts) {
        this.instagramPosts = instagramPosts;
        mParentActivity = parent;
    }

    @Override
    public InstagramPostsAdapter.PostItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View postView = inflater.inflate(R.layout.layout_item_post, parent, false);

        return new InstagramPostsAdapter.PostItemViewHolder(postView);
    }

    @Override
    public void onBindViewHolder(PostItemViewHolder holder, int position) {
        InstagramPost post = instagramPosts.get(position);

        setAvatar(holder, post);

        holder.mUserNameTextView.setText(post.user.userName);

        setPostTimestamp(holder, post);
        setInstagramPic(holder, post);
        setLikes(holder, post);
        setCaption(holder, post);
        setComments(holder, post);
    }

    private void setupShareIntent(final PostItemViewHolder holder, final InstagramPost post) {
        holder.mSharePostImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(mParentActivity, v);
                popup.getMenuInflater().inflate(R.menu.popup_share, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.mnuItemShare:
                                // Get access to the URI for the bitmap
                                Uri bmpUri = Utils.getLocalBitmapUri(holder.mPostPicImageView, post.mediaId);
                                if (bmpUri != null) {
                                    // Construct a ShareIntent with link to image
                                    Intent shareIntent = new Intent();
                                    shareIntent.setAction(Intent.ACTION_SEND);
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                                    shareIntent.setType("image/*");
                                    // Launch sharing dialog for image
                                    mParentActivity.startActivity(Intent.createChooser(shareIntent, "Share Image"));
                                } else {
                                    // ...sharing failed, handle error
                                }
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popup.show();
            }
        });
    }

    private void setComments(final PostItemViewHolder holder, final InstagramPost post) {
        holder.mCommentsLinearLayout.removeAllViews();

        holder.mViewAllCommentsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentsIntent = new Intent(mParentActivity, CommentsActivity.class);
                commentsIntent.putExtra("postId", post.mediaId);
                mParentActivity.startActivity(commentsIntent);
            }
        });

        if (post.commentsCount <= COMMENTS_THRESHOLD) {
            holder.mViewAllCommentsTextView.setVisibility(View.INVISIBLE);
        } else {
            holder.mViewAllCommentsTextView.setText(String.format(VIEW_ALL_COMMENTS_TEMPLATE, post.commentsCount));
            holder.mViewAllCommentsTextView.setVisibility(View.VISIBLE);
        }

        Context context = holder.mCommentsLinearLayout.getContext();

        for (int i = 0; i < COMMENTS_THRESHOLD && i < post.comments.size(); i++) {
            InstagramComment comment = post.comments.get(i);

            TextView commentView = (TextView) LinearLayout.inflate(context, R.layout.layout_item_text_comment, null);
            commentView.setText(Utils.prependWithBlueUsername(context, comment.user.userName, comment.text));

            holder.mCommentsLinearLayout.addView(commentView);
        }
    }

    private void setPostTimestamp(PostItemViewHolder holder, InstagramPost post) {
        holder.mTimestampTextView.setText(
                DateUtils.getRelativeTimeSpanString(
                        post.createdTime * DateUtils.SECOND_IN_MILLIS,
                        System.currentTimeMillis(),
                        DateUtils.SECOND_IN_MILLIS
                ).toString()
        );
    }

    private void setAvatar(PostItemViewHolder holder, InstagramPost post) {
        ImageView avatarImageView = holder.mAvatarImageView;
        Picasso.with(avatarImageView.getContext())
                .load(post.user.profilePictureUrl)
                .placeholder(R.drawable.gray_oval)
                .fit()
                .into(avatarImageView);
    }

    private void setCaption(PostItemViewHolder holder, InstagramPost post) {
        if (TextUtils.isEmpty(post.caption)) {
            holder.mCaptionTextView.setVisibility(View.GONE);
            return;
        }

        Context context = holder.mCaptionTextView.getContext();

        // color mentions inside the caption
        SpannableString caption = new SpannableString(post.caption);
        int startIndex = post.caption.indexOf(post.user.userName);

        // TODO: handle multiple mentions
        if (startIndex > -1) {
            caption.setSpan(Utils.getBlueForegroundColorSpan(context),
                    startIndex,
                    startIndex + post.user.userName.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            );
        }

        // set the text of the view
        holder.mCaptionTextView.setText(Utils.prependWithBlueUsername(context, post.user.userName, caption), TextView.BufferType.NORMAL);
        holder.mCaptionTextView.setVisibility(View.VISIBLE);
    }

    private void setLikes(PostItemViewHolder holder, InstagramPost post) {
        holder.mLikesTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_small_heart_filled, 0, 0, 0);
        holder.mLikesTextView.setText(Utils.formatNumberForDisplay(post.likesCount) + " likes");
    }

    private void setInstagramPic(final PostItemViewHolder holder, final InstagramPost post) {
        ImageView postPicImageView = holder.mPostPicImageView;
        Context context = postPicImageView.getContext();

        postPicImageView.setMinimumWidth(post.image.imageWidth);
        postPicImageView.setMinimumHeight(post.image.imageHeight);

        Picasso.with(context)
                .load(post.image.imageUrl)
                .placeholder(R.drawable.gray_rectangle)
                .into(postPicImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        setupShareIntent(holder, post);
                    }

                    @Override
                    public void onError() {

                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return instagramPosts.size();
    }
}
