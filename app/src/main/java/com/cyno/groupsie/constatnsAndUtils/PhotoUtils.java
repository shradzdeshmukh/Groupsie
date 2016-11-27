package com.cyno.groupsie.constatnsAndUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.widget.Toast;

import com.cyno.groupsie.Interfaces.IProgressListner;
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

    public static void getAllPhotos(final Context context, String albumID, final IProgressListner progressListner) {
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("/" + Photo.F_TABLE_NAME + "/");
        final Query query = mDatabase.orderByChild(Photo.C_ALBUM_ID).equalTo(albumID);

        valueListnerPhotos = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    getSinglePhotoAndStoreLocally(dataSnapshot, context, progressListner);
                    Log.d("progress", "done loading");
                    progressListner.onDataLoaded();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mDatabase.removeEventListener(valueListnerPhotos);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mDatabase.removeEventListener(valueListnerPhotos);
                progressListner.onDataLoaded();
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        query.addListenerForSingleValueEvent(valueListnerPhotos);

    }

    private static void getSinglePhotoAndStoreLocally(DataSnapshot dataSnapshot, Context context, IProgressListner progressListner) throws IOException {
        for (DataSnapshot photo : dataSnapshot.getChildren()) {
            Photo.getPhotoDataAndStoreLocally(context, photo, progressListner);
        }
    }

    public static void savePicToFile(final Photo photo, final Context context /*, final IProgressListner progressListner*/) {


        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.d("async", "called asynctask");
                photo.setState(Photo.state.STATE_DOWNLOADING.ordinal());
                Photo.insertOrUpdate(context, photo);
//                progressListner.showProgress();
            }


            @Override
            protected String doInBackground(Void... params) {
                File file = null;
                try {
                    Log.d("imagees", "loading " + photo.getPhotoServerUrl());
                    Bitmap bitmap = Picasso.with(context).load(photo.getPhotoServerUrl()).get();
                    file = ImageUtils.getImageFile(photo.getPhotoId());
                    FileOutputStream ostream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                    ImageUtils.compressImage(context, file);
                    ostream.close();
                    bitmap.recycle();
                    return file.getPath();
                } catch (IOException e) {
                    photo.setState(Photo.state.STATE_ERROR_DOWNLOADING.ordinal());
                    Photo.insertOrUpdate(context, photo);

                    Log.d("imagees", "exception " + e.getMessage());

                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String bitmap) {
                super.onPostExecute(bitmap);
                if (bitmap == null) {
                    photo.setState(Photo.state.STATE_ERROR_DOWNLOADING.ordinal());
                    Photo.insertOrUpdate(context, photo);

                } else {
                    Log.d("imagees", "url =  " + bitmap);
                    photo.setPhotoLocalUrl(bitmap);
                    photo.setProgressSize(-1);
                    photo.setState(Photo.state.STATE_DOWNLOADED.ordinal());
                    Photo.insertOrUpdate(context, photo);
                    Log.d("amazon", "local pic = " + photo.toString());
//                progressListner.onDataLoaded();
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

    public static void setProminentColor(final Context context, final Photo photo) {
        Bitmap mBitmap = BitmapFactory.decodeFile(photo.getPhotoLocalUrl());
        Palette.from(mBitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Log.d("color", "" + palette.getDarkMutedSwatch().getBodyTextColor());
                photo.setProminentColor(palette.getMutedColor(Color.GRAY));
                Photo.insertOrUpdate(context, photo);
            }
        });
    }
}
