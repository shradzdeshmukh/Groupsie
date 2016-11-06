package com.cyno.groupsie.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.cyno.groupsie.constatnsAndUtils.AmazonUtils;
import com.cyno.groupsie.constatnsAndUtils.PhotoUtils;
import com.cyno.groupsie.database.PhotosTable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

/**
 * Created by hp on 14-10-2016.
 */
public class Photo implements Parcelable {

    public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel source) {
            return new Photo(source);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
    public static final String F_TABLE_NAME = "Photos";
    public static final String C_PHOTO_ID = "photo_id";
    public static final String C_ALBUM_ID = "album_id";
    public static final String C_PHOTO_URL = "photo_url";
    private String photoId;
    private String photoLocalUrl;
    private String photoServerUrl;
    private String albumId;

    public Photo() {
    }

    protected Photo(Parcel in) {
        this.photoId = in.readString();
        this.photoLocalUrl = in.readString();
        this.albumId = in.readString();
        this.photoServerUrl = in.readString();
    }

    public static void uploadAndInsert(Context context, Photo photo) {
//        uploadToFirebase(photo);
        insertOrUpdate(context, photo);
        AmazonUtils.uploadImage(context, PhotoUtils.getAmazonFolder(photo), photo);
    }

    public static void insertOrUpdate(Context context, Photo photo) {
        ContentValues values = new ContentValues();
        values.put(PhotosTable.COL_PHOTO_ID, photo.getPhotoId());
        values.put(PhotosTable.COL_ALBUM_UNIQUE_ID, photo.getAlbumId());
        if (!TextUtils.isEmpty(photo.getPhotoLocalUrl()))
            values.put(PhotosTable.COL_PHOTO_LOCAL_URL, photo.getPhotoLocalUrl());
        values.put(PhotosTable.COL_PHOTO_SERVER_URL, photo.getPhotoServerUrl());
        int count = context.getContentResolver().update(PhotosTable.CONTENT_URI, values,
                PhotosTable.COL_PHOTO_ID + " = ? ", new String[]{photo.getPhotoId()});
        if (count == 0)
            context.getContentResolver().insert(PhotosTable.CONTENT_URI, values);
    }

    public static Photo getPhoto(Cursor cursor) {
        Photo photo = new Photo();
        photo.setPhotoId(cursor.getString(cursor.getColumnIndex(PhotosTable.COL_PHOTO_ID)));
        photo.setAlbumId(cursor.getString(cursor.getColumnIndex(PhotosTable.COL_ALBUM_UNIQUE_ID)));
        photo.setPhotoLocalUrl(cursor.getString(cursor.getColumnIndex(PhotosTable.COL_PHOTO_LOCAL_URL)));
        photo.setPhotoServerUrl(cursor.getString(cursor.getColumnIndex(PhotosTable.COL_PHOTO_SERVER_URL)));
        return photo;
    }

    private static void uploadToFirebase(Photo photo) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String dbPath = photo.getPhotoId().replace(".", "");
        mDatabase.child(F_TABLE_NAME).child(dbPath).child(C_PHOTO_ID).setValue(photo.getPhotoId());
        mDatabase.child(F_TABLE_NAME).child(dbPath).child(C_ALBUM_ID).setValue(photo.getAlbumId());
        mDatabase.child(F_TABLE_NAME).child(dbPath).child(C_PHOTO_URL).setValue(photo.getPhotoServerUrl());
    }

    public static void getPhotoDataAndStoreLocally(Context context, DataSnapshot dataSnapshot) throws IOException {
        Log.d("photo", dataSnapshot.toString());
        Photo photo = new Photo();
        photo.setPhotoId(dataSnapshot.child(C_PHOTO_ID).getValue().toString());
        photo.setAlbumId(dataSnapshot.child(C_ALBUM_ID).getValue().toString());
        photo.setPhotoServerUrl(dataSnapshot.child(C_PHOTO_URL).getValue().toString());
        if (!isPhotoLocallyPresent(photo.getPhotoId(), context)) {
            PhotoUtils.savePicToFile(photo, context);
            Photo.insertOrUpdate(context, photo);
        }
    }

    private static boolean isPhotoLocallyPresent(String photoId, Context context) {
        boolean isPresent = false;
        Cursor cursor = context.getContentResolver().query(PhotosTable.CONTENT_URI, new
                        String[]{PhotosTable.COL_PHOTO_LOCAL_URL}, PhotosTable.COL_PHOTO_ID + " = ? ",
                new String[]{photoId}, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                isPresent = !TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex(PhotosTable.COL_PHOTO_LOCAL_URL)));
            }
            cursor.close();
        }
        return isPresent;
    }

    public static void syncToFirebase(Context context, Photo mLocalPhoto) {
        insertOrUpdate(context, mLocalPhoto);
        uploadToFirebase(mLocalPhoto);
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getPhotoLocalUrl() {
        return photoLocalUrl;
    }

    public void setPhotoLocalUrl(String photoLocalUrl) {
        this.photoLocalUrl = photoLocalUrl;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.photoId);
        dest.writeString(this.photoLocalUrl);
        dest.writeString(this.albumId);
        dest.writeString(this.photoServerUrl);
    }

    public String getPhotoServerUrl() {
        return photoServerUrl;
    }

    public void setPhotoServerUrl(String photoServerUrl) {
        this.photoServerUrl = photoServerUrl;
    }


    @Override
    public String toString() {
        return "Photo{" +
                "photoId='" + photoId + '\'' +
                ", photoLocalUrl='" + photoLocalUrl + '\'' +
                ", photoServerUrl='" + photoServerUrl + '\'' +
                ", albumId='" + albumId + '\'' +
                '}';
    }
}
