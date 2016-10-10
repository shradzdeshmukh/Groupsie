package com.cyno.groupsie.constatnsAndUtils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.cyno.groupsie.R;

/**
 * Created by hp on 31-08-2016.
 */
public class AppUtils {

    public static boolean isVersionAllowed(Context context) {
        return getAllowedAppVersion(context) <= getActualAppVersion(context);
    }

    private static int getActualAppVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void putVersionNumber(int version, Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().
                putInt(Constants.KEY_APP_VERSION, version).commit();
    }

    private static int getAllowedAppVersion(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(Constants.KEY_APP_VERSION, -1);
    }

    public static void ShowUpgradeDialog(final Context context) {
        DialogHelper.ShowDialog(context, R.string.upgrade_title,
                R.string.upgrade_msg, R.string.upgrade,
                R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppUtils.takeToPlaystore(context);
                    }
                });
    }

    private static void takeToPlaystore(Context context) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + context.getPackageName())));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    public static String getDPUrl(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).
                getString(Constants.USER_DP_URL, "");
    }
}
