package com.codepath.instagram.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
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
import android.widget.Toast;

import com.codepath.instagram.R;
import com.codepath.instagram.activities.CommentsActivity;
import com.codepath.instagram.activities.HomeActivity;
import com.codepath.instagram.helpers.DeviceDimensionsHelper;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramComment;
import com.codepath.instagram.models.InstagramPost;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public static Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    private void setupShareIntent(final PostItemViewHolder holder, final InstagramPost post) {
        holder.mSharePostImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Get access to the URI for the bitmap
                Uri bmpUri = InstagramPostsAdapter.getLocalBitmapUri(holder.mPostPicImageView);
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
