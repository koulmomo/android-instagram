package com.codepath.instagram.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.codepath.instagram.core.MainApplication;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramPost;
import com.codepath.instagram.models.InstagramPosts;
import com.codepath.instagram.networking.InstagramClient;
import com.codepath.instagram.persistence.InstagramClientDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by koulmomo on 12/3/15.
 */
public class HomeService extends IntentService {
    public static final String ACTION = "com.codepath.instagram.services.HomeService";

    public static final int FETCH_HOME_POSTS_OK = 0;
    public static final int FETCH_HOME_POSTS_FAIL = 1;

    private AsyncHttpClient mClient = new SyncHttpClient();

    public HomeService() {
        super("InstagramHomeService");
    }

    private void broadCastSuccess(List<InstagramPost> posts, boolean wasFromNetworkCall) {
        Intent i = setResultCode(new Intent(ACTION), FETCH_HOME_POSTS_OK);
        i.putExtra("resultValue", new InstagramPosts(posts));
        i.putExtra("wasFromNetworkCall", wasFromNetworkCall);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    private void broadcastFailure(int statusCode) {
        Intent i = setResultCode(new Intent(ACTION), FETCH_HOME_POSTS_FAIL);
        i.putExtra("isError", true);
        i.putExtra("statusCode", statusCode);

        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    private Intent setResultCode(Intent i, int resultCode) {
        i.putExtra("resultCode", resultCode);
        return i;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        InstagramClient asyncRestClient = MainApplication.getRestClient();

        if (!asyncRestClient.isNetworkAvailable()) {
            broadCastSuccess(MainApplication.getDBClient().getAllInstagramPosts(), false);
            return;
        }

        mClient.get(this,
                asyncRestClient.getHomeFeedUrl(),
                new RequestParams("access_token", asyncRestClient.getToken()),
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        List<InstagramPost> posts = Utils.decodePostsFromJsonResponse(response);

                        InstagramClientDatabase db = MainApplication.getDBClient();
                        db.emptyAllTables();
                        db.addInstagramPosts(posts);
                        broadCastSuccess(posts, true);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        broadcastFailure(statusCode);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        broadcastFailure(statusCode);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        broadcastFailure(statusCode);
                    }
                }
        );
    }
}
