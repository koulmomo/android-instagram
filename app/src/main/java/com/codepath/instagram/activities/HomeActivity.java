package com.codepath.instagram.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.instagram.R;
import com.codepath.instagram.adapters.InstagramPostsAdapter;
import com.codepath.instagram.helpers.DividerItemDecoration;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramPost;
import com.codepath.instagram.networking.InstagramClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    private List<InstagramPost> mInstagramPosts = new ArrayList<InstagramPost>();
    private InstagramPostsAdapter mInstagramPostsAdapter;
    private RecyclerView rvInstagramPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Lookup the recyclerview in activity layout
        rvInstagramPosts = (RecyclerView) findViewById(R.id.rvInstagramPosts);

        rvInstagramPosts.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST)
        );

        mInstagramPostsAdapter = new InstagramPostsAdapter(this, mInstagramPosts);

        fetchPosts();

        // Attach the adapter to the recyclerview to populate items
        rvInstagramPosts.setAdapter(mInstagramPostsAdapter);
        rvInstagramPosts.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchPosts() {
        InstagramClient.getPopularFeed(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                List<InstagramPost> posts = Utils.decodePostsFromJsonResponse(response);

                mInstagramPosts.clear();
                mInstagramPosts.addAll(posts);
                mInstagramPostsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(HomeActivity.this,
                        "Error fetching popular posts.\nStatus Code: " + statusCode,
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}
