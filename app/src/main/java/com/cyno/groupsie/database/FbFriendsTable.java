package com.cyno.groupsie.database;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


public class FbFriendsTable {
    public static final String TABLE_NAME = "Friend";

    public static final Uri CONTENT_URI = Uri.parse("content://" + GroupsieContentProvider.AUTHORITY
            + "/" + TABLE_NAME);

    public static final String COL_ID = "_id";
    public static final String COL_FRIEND_NAME = "name";
    public static final String COL_FB_ID = "fb_id";
    public static final String COL_FB_PROFILE_PIC_URL = "profile_url";

    private static final String DATABASE_CREATE_NEW = "create table "
            + TABLE_NAME
            + "("
            + COL_ID + " integer primary key autoincrement, "
            + COL_FRIEND_NAME + " TEXT , "
            + COL_FB_ID + " TEXT , "
            + COL_FB_PROFILE_PIC_URL + " TEXT  "

            + ");";


    public static void onCreate(SQLiteDatabase mDatabase) {
        mDatabase.execSQL(DATABASE_CREATE_NEW);
    }

    public static void onUpdate(SQLiteDatabase mDatabase, int oldVer, int newVer) {
    }


}
