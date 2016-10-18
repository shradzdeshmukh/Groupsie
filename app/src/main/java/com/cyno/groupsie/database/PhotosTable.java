package com.cyno.groupsie.database;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


public class PhotosTable {
    public static final String TABLE_NAME = "Photo";

    public static final Uri CONTENT_URI = Uri.parse("content://" + GroupsieContentProvider.AUTHORITY
            + "/" + TABLE_NAME);

    public static final String COL_ID = "_id";
    public static final String COL_PHOTO_ID = "photo_id";
    public static final String COL_PHOTO_LOCAL_URL = "local_url";
    public static final String COL_PHOTO_SERVER_URL = "server_url";
    public static final String COL_ALBUM_UNIQUE_ID = "album_id";

    private static final String DATABASE_CREATE_NEW = "create table "
            + TABLE_NAME
            + "("
            + COL_ID + " integer primary key autoincrement, "
            + COL_PHOTO_ID + " TEXT, "
            + COL_PHOTO_LOCAL_URL + " TEXT, "
            + COL_PHOTO_SERVER_URL + " TEXT, "
            + COL_ALBUM_UNIQUE_ID + " TEXT  "
            + ");";


    public static void onCreate(SQLiteDatabase mDatabase) {
        mDatabase.execSQL(DATABASE_CREATE_NEW);
    }

    public static void onUpdate(SQLiteDatabase mDatabase, int oldVer, int newVer) {
    }


}
