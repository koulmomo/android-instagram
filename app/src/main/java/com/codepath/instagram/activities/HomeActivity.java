package com.codepath.instagram.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.instagram.R;
import com.codepath.instagram.adapters.InstagramPostsAdapter;
import com.codepath.instagram.helpers.DividerItemDecoration;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    private List<InstagramPost> instagramPosts;
    private InstagramPostsAdapter instagramPostsAdapter;
    private RecyclerView rvInstagramPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        instagramPosts = fetchPosts();

        // handle error state
        if (instagramPosts == null) {
            return;
        }

        // Lookup the recyclerview in activity layout
        rvInstagramPosts = (RecyclerView) findViewById(R.id.rvInstagramPosts);
        rvInstagramPosts.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST)
        );

        instagramPostsAdapter = new InstagramPostsAdapter(instagramPosts);

        // Attach the adapter to the recyclerview to populate items
        rvInstagramPosts.setAdapter(instagramPostsAdapter);
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

    public List<InstagramPost> fetchPosts() {
        try {
            return Utils.decodePostsFromJsonResponse(Utils.loadJsonFromAsset(this, "popular.json"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
