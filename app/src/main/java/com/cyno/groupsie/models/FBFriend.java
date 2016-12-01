package com.cyno.groupsie.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.cyno.groupsie.database.FbFriendsTable;

import java.util.ArrayList;

/**
 * Created by hp on 24-11-2016.
 */
public class FBFriend {
    private String name;
    private String id;
    private String profilePicUrl;

    private boolean isSelected;

    public static void insertOrUpdate(Context context, FBFriend friend) {
        ContentValues values = new ContentValues();
        values.put(FbFriendsTable.COL_FB_ID, friend.getId());
        values.put(FbFriendsTable.COL_FRIEND_NAME, friend.getName());
        values.put(FbFriendsTable.COL_FB_PROFILE_PIC_URL, friend.getProfilePicUrl());
        int count = context.getContentResolver().update(FbFriendsTable.CONTENT_URI, values,
                FbFriendsTable.COL_FB_ID + " = ? ", new String[]{friend.getId()});
        if (count == 0)
            context.getContentResolver().insert(FbFriendsTable.CONTENT_URI, values);
    }

    public static ArrayList<FBFriend> getAllFriends(Cursor cursor) {
        ArrayList<FBFriend> friendArrayList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                FBFriend friend = new FBFriend();
                friend.setName(cursor.getString(cursor.getColumnIndex(FbFriendsTable.COL_FRIEND_NAME)));
                friend.setId(cursor.getString(cursor.getColumnIndex(FbFriendsTable.COL_FB_ID)));
                friend.setProfilePicUrl(cursor.getString(cursor.getColumnIndex(FbFriendsTable.COL_FB_PROFILE_PIC_URL)));
                friendArrayList.add(friend);
            }
            cursor.close();
        }
        return friendArrayList;
    }

    public static FBFriend getFriend(String friendId, Context context) {
        FBFriend friend = null;
        Cursor cursor = context.getContentResolver().query(FbFriendsTable.CONTENT_URI, null,
                FbFriendsTable.COL_FB_ID + " = ? ", new String[]{friendId}, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                friend = getFriend(cursor);
            }
            cursor.close();
        }
        return friend;
    }

    private static FBFriend getFriend(Cursor cursor) {
        FBFriend friend = new FBFriend();
        friend.setId(cursor.getString(cursor.getColumnIndex(FbFriendsTable.COL_FB_ID)));
        friend.setProfilePicUrl(cursor.getString(cursor.getColumnIndex(FbFriendsTable.COL_FB_PROFILE_PIC_URL)));
        friend.setName(cursor.getString(cursor.getColumnIndex(FbFriendsTable.COL_FRIEND_NAME)));
        return friend;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
