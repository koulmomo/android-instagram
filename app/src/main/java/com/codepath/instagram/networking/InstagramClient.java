package com.codepath.instagram.networking;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by koulmomo on 12/1/15.
 */
public class InstagramClient {
    private final static String CLIENT_ID = "e05c462ebd86446ea48a5af73769b602";
    private final static AsyncHttpClient client = new AsyncHttpClient();

    private final static String BASE_URL = "https://api.instagram.com/v1";

    public static void getPopularFeed(JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams("client_id", CLIENT_ID);

        client.get(InstagramClient.getPopularPostsUrl(), params, responseHandler);
    }

    public static String getPopularPostsUrl() {
        return BASE_URL + "/media/popular";
    }
}
