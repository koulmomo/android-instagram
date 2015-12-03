package com.codepath.instagram.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.instagram.R;
import com.codepath.instagram.adapters.SearchTagResultsAdapter;
import com.codepath.instagram.core.MainApplication;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramSearchTag;
import com.codepath.instagram.models.InstagramUser;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchTagsResultFragment extends Fragment {
    
    public SearchTagsResultFragment() {
        // Required empty public constructor
    }

    @Bind(R.id.rvTags)
    RecyclerView mTagsRV;

    private List<InstagramSearchTag> mTags = new ArrayList<>();
    private SearchTagResultsAdapter mTagsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_tags_result, container, false);

        ButterKnife.bind(this, view);

        mTagsAdapter = new SearchTagResultsAdapter(getActivity(), mTags);

        mTagsRV.setLayoutManager(new LinearLayoutManager(getContext()));
        mTagsRV.setAdapter(mTagsAdapter);

        return view;
    }

    public static Fragment newInstance() {
        SearchTagsResultFragment fragment = new SearchTagsResultFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    return true;
                }

                MainApplication.getRestClient().getTagSearchResults(newText, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        List<InstagramSearchTag> tags = Utils.decodeSearchTagsFromJsonResponse(response);

                        mTags.clear();
                        mTags.addAll(tags);
                        mTagsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Toast.makeText(getActivity(), "bad stuff", Toast.LENGTH_LONG).show();
                    }
                });

                return true;
            }
        });


        super.onCreateOptionsMenu(menu, inflater);
    }
}
