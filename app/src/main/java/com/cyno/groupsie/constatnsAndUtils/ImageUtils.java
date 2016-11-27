package com.cyno.groupsie.constatnsAndUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;

/**
 * Created by hp on 13-10-2016.
 */
public class ImageUtils {

    public static final int REQUEST_TAKE_PHOTO = 100;
    private static final String DIRECTORY_NAME = "Groupsie";
    private static final String KEY_CURRENT_IMAGE_PATH = "image";

    public static File getImageFile(String photoId) throws IOException {
        // Create an image file name
        String imageFileName = "Groupsie_" + photoId + ".jpg";
        // Save a file: path for use with ACTION_VIEW intents
        checkOrCreateDirectory();
        return new File(Environment.getExternalStorageDirectory() + "/" + DIRECTORY_NAME + "/" + imageFileName);
    }

    private static void checkOrCreateDirectory() {
        File file = new File(Environment.getExternalStorageDirectory() + "/" + DIRECTORY_NAME);
        if (!file.exists())
            file.mkdir();
    }


    public static File dispatchTakePictureIntent(Context context, String photoId) throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure` that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = getImageFile(photoId);
            Log.d("file", "file name = " + photoFile.getName());
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                ((Activity) context).startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                return photoFile;
            }
        }
        return null;
    }

    public static Bitmap getCapturedPic(Context context) throws IOException {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(getCurrentImageName(context), bmOptions);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inPurgeable = true;
        return BitmapFactory.decodeFile(getCurrentImageName(context), bmOptions);
    }

    public static void putCurrentImageName(String imageName, Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(KEY_CURRENT_IMAGE_PATH, imageName).commit();
    }

    public static String getCurrentImageName(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_CURRENT_IMAGE_PATH, null);
    }

    public static void compressImage(Context context, File file) {
        new Compressor.Builder(context)
                .setMaxWidth(getMaxWidth())
                .setMaxHeight(getMaxHeight())
                .setQuality(getQuality()).build()
                .compressToFile(file);

    }

    public static File compressToUploadImage(Context context, File file) {
        return new Compressor.Builder(context)
                .setMaxWidth(getMaxWidth())
                .setMaxHeight(getMaxHeight())
                .setQuality(getQuality()).build()
                .compressToFile(file);

    }


    private static int getQuality() {
        return 100;
    }

    private static float getMaxHeight() {
        return 640;
    }

    private static float getMaxWidth() {
        return 480;
    }
}
