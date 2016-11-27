package com.cyno.groupsie.constatnsAndUtils;

import android.content.Context;
import android.util.Log;

import com.cyno.groupsie.models.FBFriend;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hp on 24-11-2016.
 */
public class FBUtils {

    private static final String DP_WIDTH = "500";
    private static final String DP_HEIGHT = "500";
    private static final java.lang.String KEY_NAME = "name";
    private static final java.lang.String KEY_ID = "id";
    private static final String KEY_DATA = "data";

    public static void GetFriendList(final Context context, String fbUserId) {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + fbUserId + "/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.d("friends", response.toString());
                        try {
                            parseAndInsertFriends(context, response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }

    private static void parseAndInsertFriends(Context context, GraphResponse response) throws JSONException {
        JSONObject object = response.getJSONObject();
        JSONArray friendArr = object.getJSONArray(KEY_DATA);
        for (int index = 0; index < friendArr.length(); ++index) {
            JSONObject friend = friendArr.getJSONObject(index);
            FBFriend fbFriend = new FBFriend();
            fbFriend.setName(friend.optString(KEY_NAME));
            fbFriend.setId(friend.optString(KEY_ID));
            fbFriend.setProfilePicUrl(getProfilePicUrl(fbFriend.getId()));
            FBFriend.insertOrUpdate(context, fbFriend);
        }
    }

    public static String getProfilePicUrl(String userId) {
        return "https://graph.facebook.com/" + userId + "/picture?type=large&width=" + DP_WIDTH + "&height=" + DP_HEIGHT;
    }
}
