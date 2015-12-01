package com.codepath.instagram.adapters;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.instagram.R;
import com.codepath.instagram.helpers.Constants;
import com.codepath.instagram.helpers.DeviceDimensionsHelper;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramPost;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by koulmomo on 11/30/15.
 */
public class InstagramPostsAdapter extends RecyclerView.Adapter<InstagramPostsAdapter.PostItemViewHolder> {
    private List<InstagramPost> instagramPosts;

    public static class PostItemViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImageView;
        TextView userNameTextView;
        TextView timestampTextView;

        ImageView postPicImageView;

        TextView likesTextView;

        TextView captionTextView;

        public PostItemViewHolder(View itemView) {
            super(itemView);

            avatarImageView = (ImageView) itemView.findViewById(R.id.ivAvatar);
            userNameTextView = (TextView) itemView.findViewById(R.id.tvUserName);
            timestampTextView = (TextView) itemView.findViewById(R.id.tvTimestamp);

            postPicImageView = (ImageView) itemView.findViewById(R.id.ivPostPic);

            likesTextView = (TextView) itemView.findViewById(R.id.tvLikes);
            captionTextView = (TextView) itemView.findViewById(R.id.tvCaption);
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
        InstagramPost post = instagramPosts.get(position);

        ImageView avatarImageView = holder.avatarImageView;
        Picasso.with(avatarImageView.getContext())
                .load(post.user.profilePictureUrl)
                .placeholder(R.drawable.gray_oval)
                .fit()
                .into(avatarImageView);

        holder.userNameTextView.setText(post.user.userName);

        holder.timestampTextView.setText(
                DateUtils.getRelativeTimeSpanString(
                        post.createdTime * 1000,
                        System.currentTimeMillis(),
                        DateUtils.SECOND_IN_MILLIS
                ).toString()
        );

        setInstagramPic(holder, post);
        setLikes(holder, post);
        setCaption(holder, post);
    }

    private void setCaption(PostItemViewHolder holder, InstagramPost post) {
        if (post.caption == null || post.caption.length() < 1) {
            holder.captionTextView.setVisibility(View.INVISIBLE);
            return;
        }

        holder.captionTextView.setVisibility(View.VISIBLE);

        ForegroundColorSpan blueForeGroundColorSpan = new ForegroundColorSpan(
                holder.captionTextView.getContext().getResources().getColor(R.color.blue_text));

        SpannableString caption = new SpannableString(post.caption);

        int startIndex = post.caption.indexOf(post.user.userName);

        // TODO: handle multiple mentions
        if (startIndex > -1) {
            caption.setSpan(blueForeGroundColorSpan,
                    startIndex,
                    startIndex + post.user.userName.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            );
        }

        holder.captionTextView.setText(caption);

    }

    private void setLikes(PostItemViewHolder holder, InstagramPost post) {
        int leftDrawableId = post.likesCount > 0 ? R.drawable.ic_small_heart_filled : R.drawable.ic_heart;

        holder.likesTextView.setCompoundDrawablesWithIntrinsicBounds(leftDrawableId, 0, 0, 0);
        holder.likesTextView.setText(Utils.formatNumberForDisplay(post.likesCount) + " likes");
    }

    private void setInstagramPic(PostItemViewHolder holder, InstagramPost post) {
        ImageView postPicImageView = holder.postPicImageView;
        Context context = postPicImageView.getContext();

        postPicImageView.setMinimumWidth(post.image.imageWidth);
        postPicImageView.setMinimumHeight(post.image.imageHeight);

        int imageWidth = postPicImageView.getWidth();

        if (imageWidth <= 0) {
            imageWidth = DeviceDimensionsHelper.getDisplayWidth(context);
        }

        Picasso.with(context)
                .load(post.image.imageUrl)
                // TODO: instead of resize, scale Y
                .placeholder(R.drawable.gray_rectangle)
                .resize(imageWidth, 0)
                .into(postPicImageView);
    }

    @Override
    public int getItemCount() {
        return instagramPosts.size();
    }
}
