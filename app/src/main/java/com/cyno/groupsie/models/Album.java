package com.cyno.groupsie.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.cyno.groupsie.database.AlbumTable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by hp on 10-09-2016.
 */

@IgnoreExtraProperties
public class Album implements Parcelable {


    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel source) {
            return new Album(source);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };
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
    private boolean isRequestAccepted;


    public Album() {
    }

    protected Album(Parcel in) {
        this.albumId = in.readString();
        this.albumName = in.readString();
        this.coverPicUrl = in.readString();
        this.grade = in.readInt();
        this.createDate = in.readLong();
        this.isRequestAccepted = in.readByte() != 0;
    }

    private static void writeAlbum(Album album) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(F_TABLE_NAME).child(album.getAlbumId()).child(C_ALBUM_NAME).setValue(album.getAlbumName());
        mDatabase.child(F_TABLE_NAME).child(album.getAlbumId()).child(C_COVER_PIC_URL).setValue(album.getCoverPicUrl() != null ? album.getCoverPicUrl() : "");
        mDatabase.child(F_TABLE_NAME).child(album.getAlbumId()).child(C_ALBUM_GRADE).setValue(album.getGrade());
        mDatabase.child(F_TABLE_NAME).child(album.getAlbumId()).child(C_CREATE_TIME).setValue(album.getCreateDate());
    }

    public static void getAlbumData(Context context, DataSnapshot dataSnapshot) {
        Log.d("album", dataSnapshot.toString());
        Album album = new Album();
        album.setAlbumName(dataSnapshot.child(C_ALBUM_NAME).getValue().toString());
        album.setCreateDate(Long.valueOf(dataSnapshot.child(C_CREATE_TIME).getValue().toString()));
        album.setGrade(Integer.valueOf(dataSnapshot.child(C_ALBUM_GRADE).getValue().toString()));
        album.setAlbumId(dataSnapshot.getKey());
        album.setCoverPicUrl(dataSnapshot.child(C_COVER_PIC_URL).getValue().toString());
        Album.insertOrUpdate(context, album);
    }

    public static Album getAlbum(Cursor cursor) {
        Album album = new Album();
        album.setAlbumId(cursor.getString(cursor.getColumnIndex(AlbumTable.COL_ALBUM_UNIQUE_ID)));
        album.setAlbumName(cursor.getString(cursor.getColumnIndex(AlbumTable.COL_ALBUM_NAME)));
        album.setCreateDate(cursor.getLong(cursor.getColumnIndex(AlbumTable.COL_CREATE_DATE)));
        album.setCoverPicUrl(cursor.getString(cursor.getColumnIndex(AlbumTable.COL_COVER_PIC)));
        album.setGrade(cursor.getInt(cursor.getColumnIndex(AlbumTable.COL_GRADE)));
        album.setRequestAccepted(cursor.getString(cursor.getColumnIndex(AlbumTable.COL_IS_REQ_ACCEPTED)).equalsIgnoreCase("1"));
        return album;
    }

    public static void insertOrUpdate(Context context, Album album) {
        ContentValues values = new ContentValues();
        values.put(AlbumTable.COL_ALBUM_NAME, album.getAlbumName());
        values.put(AlbumTable.COL_ALBUM_UNIQUE_ID, album.getAlbumId());
        values.put(AlbumTable.COL_COVER_PIC, album.getCoverPicUrl());
        values.put(AlbumTable.COL_GRADE, album.getGrade());
        values.put(AlbumTable.COL_CREATE_DATE, album.getCreateDate());
        values.put(AlbumTable.COL_IS_REQ_ACCEPTED, album.isRequestAccepted());
        int count = context.getContentResolver().update(AlbumTable.CONTENT_URI, values,
                AlbumTable.COL_ALBUM_UNIQUE_ID + " = ? ", new String[]{album.getAlbumId()});
        if (count == 0)
            context.getContentResolver().insert(AlbumTable.CONTENT_URI, values);
    }

    public static void storeAndWriteAlbum(String userId, Album album, Context context) {
        writeAlbum(album);
        Member.writeMember(new Member(userId, album.getAlbumId()));
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

    public boolean isRequestAccepted() {
        return isRequestAccepted;
    }

    public void setRequestAccepted(boolean requestAccepted) {
        isRequestAccepted = requestAccepted;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.albumId);
        dest.writeString(this.albumName);
        dest.writeString(this.coverPicUrl);
        dest.writeInt(this.grade);
        dest.writeLong(this.createDate);
        dest.writeByte(this.isRequestAccepted ? (byte) 1 : (byte) 0);
    }
}
