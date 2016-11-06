package com.cyno.groupsie.constatnsAndUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.cyno.groupsie.models.Photo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by hp on 15-10-2016.
 */
public class PhotoUtils {
    private static ValueEventListener valueListnerPhotos;

    public static void getAllPhotos(final Context context, String albumID) {
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("/" + Photo.F_TABLE_NAME + "/");
        final Query query = mDatabase.orderByChild(Photo.C_ALBUM_ID).equalTo(albumID);

        valueListnerPhotos = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    getSinglePhotoAndStoreLocally(dataSnapshot, context);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mDatabase.removeEventListener(valueListnerPhotos);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mDatabase.removeEventListener(valueListnerPhotos);
            }
        };
        query.addListenerForSingleValueEvent(valueListnerPhotos);

    }

    private static void getSinglePhotoAndStoreLocally(DataSnapshot dataSnapshot, Context context) throws IOException {
        for (DataSnapshot photo : dataSnapshot.getChildren()) {
            Photo.getPhotoDataAndStoreLocally(context, photo);
        }
    }

    public static void savePicToFile(final Photo photo, final Context context) throws IOException {
       /* Target target = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                try {
                    File file = ImageUtils.getImageFile(context);
                    FileOutputStream ostream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,ostream);
                    ostream.close();
                    photo.setPhotoLocalUrl(file.getParent());
                    Log.d("local photo " , photo.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.d("local photo " , errorDrawable.toString());

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };*/

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    return Picasso.with(context).load(photo.getPhotoServerUrl()).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                File file = null;
                try {
                    file = ImageUtils.getImageFile(photo.getPhotoId());
                    FileOutputStream ostream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                    ImageUtils.compressImage(context, file);
                    ostream.close();
                    photo.setPhotoLocalUrl(file.getPath());
                    Photo.insertOrUpdate(context, photo);
                    Log.d("amazon", "local pic = " + photo.toString());

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.execute();

    }

    public static String getAmazonFolder(Photo photo) {
        return photo.getAlbumId() + "/" + photo.getPhotoId();
    }

    private static String getPhotoNameFromUr(String url) {
        String[] arry = url.split("/");
        return arry[arry.length];
    }


    public static String getPhotoId(String albumId) {
        return albumId + "_" + System.currentTimeMillis();
    }
}
