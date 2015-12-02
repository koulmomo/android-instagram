package com.codepath.instagram.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.codepath.instagram.R;
import com.codepath.instagram.adapters.InstagramCommentsAdapter;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramComment;
import com.codepath.instagram.networking.InstagramClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView mCommentsRecyclerView;
    private String mPostId;

    private List<InstagramComment> mComments = new ArrayList<>();
    private InstagramCommentsAdapter mCommentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        setTitle("Comments");

        mPostId = getIntent().getStringExtra("postId");

        mCommentsRecyclerView = (RecyclerView) findViewById(R.id.rvComments);
        mCommentsAdapter = new InstagramCommentsAdapter(mComments);

        fetchComments();

        // Attach the adapter to the recyclerview to populate items
        mCommentsRecyclerView.setAdapter(mCommentsAdapter);
        mCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void fetchComments() {
        InstagramClient.getComments(mPostId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                List<InstagramComment> comments = Utils.decodeCommentsFromJsonResponse(response);

                mComments.clear();
                mComments.addAll(comments);
                mCommentsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(CommentsActivity.this,
                        String.format("Error fetching comments for post: %s.\nStatus Code: %d", mPostId, statusCode),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}
