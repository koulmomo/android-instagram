package com.codepath.instagram.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.instagram.R;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramSearchTag;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by koulmomo on 12/2/15.
 */
public class SearchTagResultsAdapter extends RecyclerView.Adapter<SearchTagResultsAdapter.SearchTagHolder> {
    public class SearchTagHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tvTag)
        TextView mTagTV;

        @Bind(R.id.tvCount) TextView mCountTV;

        public SearchTagHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    Activity mParentActivity;
    List<InstagramSearchTag> mTags;

    public SearchTagResultsAdapter(Activity parent, List<InstagramSearchTag> tags) {
        mTags = tags;
        mParentActivity = parent;
    }

    @Override
    public SearchTagHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SearchTagResultsAdapter.SearchTagHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_item_tag_result, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(SearchTagHolder holder, int position) {
        InstagramSearchTag tag = mTags.get(position);
        holder.mTagTV.setText(tag.tag);
        holder.mCountTV.setText(Utils.formatNumberForDisplay(tag.count));
    }


    @Override
    public int getItemCount() {
        return mTags.size();
    }
}
