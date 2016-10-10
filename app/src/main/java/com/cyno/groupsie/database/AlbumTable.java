package com.cyno.groupsie.database;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


public class AlbumTable {
    public static final String ALARM_TABLE = "Tasks";

    public static final Uri CONTENT_URI = Uri.parse("content://" + GroupsieContentProvider.AUTHORITY
            + "/" + ALARM_TABLE);

    public static final String COL_ID = "_id";
    public static final String COL_ALBUM_UNIQUE_ID = "unique_id";
    public static final String COL_ALBUM_NAME = "name";
    public static final String COL_GRADE = "grade";
    public static final String COL_COVER_PIC = "cover_pic";
    public static final String COL_CREATE_DATE = "create_date";

    private static final String DATABASE_CREATE_NEW = "create table "
            + ALARM_TABLE
            + "("
            + COL_ID + " integer primary key autoincrement, "
            + COL_ALBUM_UNIQUE_ID + " TEXT , "
            + COL_ALBUM_NAME + " TEXT , "
            + COL_GRADE + " TEXT , "
            + COL_COVER_PIC + " TEXT , "
            + COL_CREATE_DATE + " TEXT  "
            + ");";


    public static void onCreate(SQLiteDatabase mDatabase) {
        mDatabase.execSQL(DATABASE_CREATE_NEW);
    }

    public static void onUpdate(SQLiteDatabase mDatabase, int oldVer, int newVer) {
    }


}
