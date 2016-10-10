package com.cyno.groupsie.models;

import android.content.ContentValues;
import android.content.Context;

import com.cyno.groupsie.database.AlbumTable;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by hp on 10-09-2016.
 */

@IgnoreExtraProperties
public class Album {

    private static final String F_TABLE_NAME = "Album";
    private static final String C_ALBUM_NAME = "name";
    private static final String C_COVER_PIC_URL = "cover";
    private static final String C_ALBUM_GRADE = "grade";
    private static final String C_CREATE_TIME = "time";

    private String albumId;
    private String albumName;
    private String coverPicUrl;
    private int grade;
    private long createDate;

    public Album() {
    }

    public Album(String albumId, String albumName, String coverPicUrl, int grade, long createDate) {
        this.albumId = albumId;
        this.albumName = albumName;
        this.coverPicUrl = coverPicUrl;
        this.grade = grade;
        this.createDate = createDate;
    }

    private static void writeAlbum(Album album) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(F_TABLE_NAME).child(album.getAlbumId()).child(C_ALBUM_NAME).setValue(album.getAlbumName());
        mDatabase.child(F_TABLE_NAME).child(album.getAlbumId()).child(C_COVER_PIC_URL).setValue(album.getCoverPicUrl());
        mDatabase.child(F_TABLE_NAME).child(album.getAlbumId()).child(C_ALBUM_GRADE).setValue(album.getGrade());
        mDatabase.child(F_TABLE_NAME).child(album.getAlbumId()).child(C_CREATE_TIME).setValue(album.getCreateDate());
    }

    private static void insertOrUpdate(Context context, Album album) {
        ContentValues values = new ContentValues();
        values.put(AlbumTable.COL_ALBUM_NAME, album.getAlbumName());
        values.put(AlbumTable.COL_ALBUM_UNIQUE_ID, album.getAlbumId());
        values.put(AlbumTable.COL_COVER_PIC, album.getCoverPicUrl());
        values.put(AlbumTable.COL_GRADE, album.getGrade());
        values.put(AlbumTable.COL_CREATE_DATE, album.getCreateDate());
        int count = context.getContentResolver().update(AlbumTable.CONTENT_URI, values,
                AlbumTable.COL_ALBUM_UNIQUE_ID + " = ? ", new String[]{album.getAlbumId()});
        if (count == 0)
            context.getContentResolver().insert(AlbumTable.CONTENT_URI, values);
    }

    public static void storeAndWriteAlbum(Album album, Context context) {
        writeAlbum(album);
        insertOrUpdate(context, album);
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getCoverPicUrl() {
        return coverPicUrl;
    }

    public void setCoverPicUrl(String coverPicUrl) {
        this.coverPicUrl = coverPicUrl;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }
}
