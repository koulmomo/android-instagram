package com.codepath.instagram.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InstagramUser implements Serializable {
    public String userName;
    public String fullName;
    public String profilePictureUrl;
    public String userId;
    public String bio;
    public String website;

    public int mediaCount;
    public int followerCount;
    public int followCount;

    public static InstagramUser fromJson(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }

        InstagramUser user = new InstagramUser();

        try {
            user.userId = jsonObject.getString("id");
            user.userName = jsonObject.getString("username");
            user.fullName = jsonObject.optString("full_name", "");
            user.profilePictureUrl = jsonObject.getString("profile_picture");
            user.bio = jsonObject.optString("bio", "");
            user.website = jsonObject.optString("website", "");

            JSONObject counts = jsonObject.optJSONObject("counts");
            if (counts != null) {
                user.mediaCount = counts.optInt("media", 0);
                user.followCount = counts.optInt("follows", 0);
                user.followerCount = counts.optInt("followed_by", 0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return user;
    }

    public static List<InstagramUser> fromJson(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }

        List<InstagramUser> users = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject;
            try {
                jsonObject = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            InstagramUser user = InstagramUser.fromJson(jsonObject);
            if (user != null) {
                users.add(user);
            }
        }
        return users;
    }

}
