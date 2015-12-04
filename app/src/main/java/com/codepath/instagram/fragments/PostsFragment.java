package com.codepath.instagram.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.instagram.R;
import com.codepath.instagram.adapters.InstagramPostsAdapter;
import com.codepath.instagram.core.MainApplication;
import com.codepath.instagram.helpers.DividerItemDecoration;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramPost;
import com.codepath.instagram.models.InstagramPosts;
import com.codepath.instagram.networking.InstagramClient;
import com.codepath.instagram.persistence.InstagramClientDatabase;
import com.codepath.instagram.services.HomeService;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

public class PostsFragment extends Fragment {
    private BroadcastReceiver mReveiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mPostsContainerSRL.setRefreshing(false);
            switch (intent.getIntExtra("resultCode", -1)) {
                case Activity.RESULT_OK:
                    InstagramPosts postsWrapper = (InstagramPosts) intent.getSerializableExtra("resultValue");

                    if (postsWrapper == null || postsWrapper.posts == null) {
                        Toast.makeText(
                                context,
                                "Intent said everything was ok but either postsWrapper OR postsWrapper.posts was null",
                                Toast.LENGTH_LONG
                        ).show();
                        return;
                    }

                    mInstagramPosts.clear();
                    mInstagramPosts.addAll(postsWrapper.posts);
                    mInstagramPostsAdapter.notifyDataSetChanged();
            }
        }
    };


    @Bind(R.id.srlPostsContainer)
    SwipeRefreshLayout mPostsContainerSRL;

    @Bind(R.id.rvInstagramPosts) RecyclerView rvInstagramPosts;

    private List<InstagramPost> mInstagramPosts = new ArrayList<InstagramPost>();
    InstagramPostsAdapter mInstagramPostsAdapter;

    public PostsFragment() {
        // Required empty public constructor
    }

    public static PostsFragment newInstance() {
        PostsFragment fragment = new PostsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LocalBroadcastManager.getInstance(this.getContext())
                .registerReceiver(mReveiver, new IntentFilter(HomeService.ACTION));

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_posts, container, false);

        ButterKnife.bind(this, view);

        rvInstagramPosts.addItemDecoration(
                new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST)
        );

        mInstagramPostsAdapter = new InstagramPostsAdapter(getActivity(), mInstagramPosts);

        // Attach the adapter to the recyclerview to populate items
        rvInstagramPosts.setAdapter(mInstagramPostsAdapter);
        rvInstagramPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        mPostsContainerSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchHomeFeed();
            }
        });

        fetchHomeFeed();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        LocalBroadcastManager.getInstance(this.getContext()).unregisterReceiver(mReveiver);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void fetchPopularPosts() {
        MainApplication.getRestClient().getPopularFeed(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                List<InstagramPost> posts = Utils.decodePostsFromJsonResponse(response);

                mInstagramPosts.clear();
                mInstagramPosts.addAll(posts);
                mInstagramPostsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(PostsFragment.this.getContext(),
                        "Error fetching popular posts.\nStatus Code: " + statusCode,
                        LENGTH_LONG
                ).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mPostsContainerSRL.setRefreshing(false);
            }
        });
    }

    public void fetchHomeFeed() {
        getActivity().startService(new Intent(getContext(), HomeService.class));
    }
}
