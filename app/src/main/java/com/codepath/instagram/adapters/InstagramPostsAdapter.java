package com.codepath.instagram.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codepath.instagram.R;
import com.codepath.instagram.activities.CommentsActivity;
import com.codepath.instagram.activities.HomeActivity;
import com.codepath.instagram.helpers.DeviceDimensionsHelper;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramComment;
import com.codepath.instagram.models.InstagramPost;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by koulmomo on 11/30/15.
 */
public class InstagramPostsAdapter extends RecyclerView.Adapter<InstagramPostsAdapter.PostItemViewHolder> {

    public static class PostItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mAvatarImageView;
        TextView mUserNameTextView;
        TextView mTimestampTextView;

        ImageView mPostPicImageView;

        TextView mLikesTextView;

        TextView mCaptionTextView;

        TextView mViewAllCommentsTextView;

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
        }

        @Override
        public void onClick(View v) {

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

        for (int i = 0; i < COMMENTS_THRESHOLD; i++) {
            InstagramComment comment = post.comments.get(i);

            TextView commentView = (TextView) LinearLayout.inflate(context, R.layout.layout_item_text_comment, null);
            commentView.setText(prependWithBlueUsername(context, comment.user.userName, comment.text));

            holder.mCommentsLinearLayout.addView(commentView);
        }
    }

    public static ForegroundColorSpan getBlueForegroundColorSpan(Context context) {
        return new ForegroundColorSpan(context.getResources().getColor(R.color.blue_text));
    }

    public static SpannableStringBuilder prependWithBlueUsername(Context context, String username, CharSequence suffix) {
        SpannableString userNamePrefix = new SpannableString(username);
        userNamePrefix.setSpan(getBlueForegroundColorSpan(context), 0, username.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        SpannableStringBuilder ssb = new SpannableStringBuilder(userNamePrefix);
        ssb.append(" ");
        ssb.append(suffix);

        return ssb;
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
            caption.setSpan(getBlueForegroundColorSpan(context),
                    startIndex,
                    startIndex + post.user.userName.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            );
        }

        // set the text of the view
        holder.mCaptionTextView.setText(prependWithBlueUsername(context, post.user.userName, caption), TextView.BufferType.NORMAL);
        holder.mCaptionTextView.setVisibility(View.VISIBLE);
    }

    private void setLikes(PostItemViewHolder holder, InstagramPost post) {
        holder.mLikesTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_small_heart_filled, 0, 0, 0);
        holder.mLikesTextView.setText(Utils.formatNumberForDisplay(post.likesCount) + " likes");
    }

    private void setInstagramPic(PostItemViewHolder holder, InstagramPost post) {
        ImageView postPicImageView = holder.mPostPicImageView;
        Context context = postPicImageView.getContext();

        postPicImageView.setMinimumWidth(post.image.imageWidth);
        postPicImageView.setMinimumHeight(post.image.imageHeight);

        int imageWidth = postPicImageView.getWidth();

        if (imageWidth <= 0) {
            imageWidth = DeviceDimensionsHelper.getDisplayWidth(context);
        }

        Picasso.with(context)
                .load(post.image.imageUrl)
                .placeholder(R.drawable.gray_rectangle)
                .into(postPicImageView);
    }

    @Override
    public int getItemCount() {
        return instagramPosts.size();
    }
}
