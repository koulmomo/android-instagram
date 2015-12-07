package com.codepath.instagram.networking;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.codepath.instagram.helpers.Constants;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.phrase.Phrase;

/**
 * Created by koulmomo on 12/1/15.
 */
public class InstagramClient extends OAuthBaseClient {
    private final static String REST_URL = "https://api.instagram.com/v1";
    private final static String REST_CONSUMER_KEY = "e05c462ebd86446ea48a5af73769b602";
    private final static String REST_CONSUMER_SECRET = "7f18a14de6c241c2a9ccc9f4a3df4b35";

    public InstagramClient(Context context) {
        super(
                context,
                InstagramApi.class,
                REST_URL,
                REST_CONSUMER_KEY,
                REST_CONSUMER_SECRET,
                Constants.REDIRECT_URI,
                Constants.SCOPE
        );
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public void getPopularFeed(JsonHttpResponseHandler responseHandler) {
        // client.get(getPopularPostsUrl(), createBaseRequestParams(), responseHandler);
        client.get(getPopularPostsUrl(), responseHandler);
    }

    public void getHomeFeed(JsonHttpResponseHandler responseHandler) {
        client.get(getHomeFeedUrl(), responseHandler);
    }

    public String getHomeFeedUrl() {
        return getApiUrl("users/self/feed");
    }

    public void getComments(String postId, JsonHttpResponseHandler responseHandler) {
        // client.get(getCommentsUrl(postId), createBaseRequestParams(), responseHandler);
        client.get(getCommentsUrl(postId), responseHandler);
    }

    public void getUserSearchResults(String query, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams("q", query);
        client.get(getApiUrl("users/search"), params, responseHandler);
    }

    public void getTagSearchResults(String query, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams("q", query);
        client.get(getApiUrl("tags/search"), params, responseHandler);
    }

    public String getCommentsUrl(String postId) {
        return getApiUrl(String.format("media/%s/comments", postId));
    }

    public String getUserProfileUrl(String user) {
        return getApiUrl(Phrase.from("users/{user_id}").put("user_id", user).format().toString());
    }

    public String getSelfUserProfileUrl() {
        return getUserProfileUrl("self");
    }

    public String getToken() {
       return this.client.getAccessToken().getToken();
    }

    public String getPopularPostsUrl() {
        return getApiUrl("media/popular");
    }
}
