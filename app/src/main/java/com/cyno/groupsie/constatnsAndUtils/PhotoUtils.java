package com.cyno.groupsie.constatnsAndUtils;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.IOException;

/**
 * Created by hp on 15-10-2016.
 */
public class PhotoUtils {
    public static String getPhotoId(String imageName, String albumId) {
        return albumId + "_" + imageName;
    }

    public static String getLocalPhotoUrl(Context context) throws IOException {
        return Uri.fromFile(new File(ImageUtils.getCurrentImageName(context))).toString();
    }
}
