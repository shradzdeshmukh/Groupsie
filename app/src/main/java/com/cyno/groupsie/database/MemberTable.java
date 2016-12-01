package com.cyno.groupsie.database;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


public class MemberTable {
    public static final String TABLE_NAME = "Members";

    public static final Uri CONTENT_URI = Uri.parse("content://" + GroupsieContentProvider.AUTHORITY
            + "/" + TABLE_NAME);

    public static final String COL_ID = "_id";
    public static final String COL_ALBUM_ID = "album_id";
    public static final String COL_USER_ID = "user_id";

    private static final String DATABASE_CREATE_NEW = "create table "
            + TABLE_NAME
            + "("
            + COL_ID + " integer primary key autoincrement, "
            + COL_ALBUM_ID + " TEXT , "
            + COL_USER_ID + " TEXT  "
            + ");";


    public static void onCreate(SQLiteDatabase mDatabase) {
        mDatabase.execSQL(DATABASE_CREATE_NEW);
    }

    public static void onUpdate(SQLiteDatabase mDatabase, int oldVer, int newVer) {
    }


}
