package com.codepath.instagram.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.instagram.R;
import com.codepath.instagram.adapters.SearchUserResultsAdapter;
import com.codepath.instagram.core.MainApplication;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramUser;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchUsersResultFragment extends Fragment implements SearchResultInterface {

    public SearchUsersResultFragment() {
        // Required empty public constructor
    }

    public static SearchUsersResultFragment newInstance() {
        SearchUsersResultFragment fragment = new SearchUsersResultFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Bind(R.id.rvSearchResults) RecyclerView mSearchResultsRV;

    private List<InstagramUser> mInstagramUsers = new ArrayList<InstagramUser>();
    SearchUserResultsAdapter mUsersAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_users_result, container, false);

        ButterKnife.bind(this, view);

        mUsersAdapter = new SearchUserResultsAdapter(getActivity(), mInstagramUsers);
        mSearchResultsRV.setAdapter(mUsersAdapter);
        mSearchResultsRV.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public boolean onSearchQuery(String query) {
        if (TextUtils.isEmpty(query)) {
            mInstagramUsers.clear();
            mUsersAdapter.notifyDataSetChanged();
            return true;
        }

        MainApplication.getRestClient().getUserSearchResults(query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                List<InstagramUser> users = Utils.decodeUsersFromJsonResponse(response);

                mInstagramUsers.clear();
                mInstagramUsers.addAll(users);
                mUsersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getActivity(), "bad stuff", Toast.LENGTH_LONG).show();
            }
        });

        return true;
    }
}
