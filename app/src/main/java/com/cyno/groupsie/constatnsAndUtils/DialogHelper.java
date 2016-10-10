package com.cyno.groupsie.constatnsAndUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by hp on 01-09-2016.
 */
public class DialogHelper {

    public static void ShowDialog(Context context, int title, int msg, int posButtonTitle, int negButtonTitle,
                                  DialogInterface.OnClickListener positiveClickListner) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(msg).
                setPositiveButton(posButtonTitle, positiveClickListner).
                setNegativeButton(negButtonTitle, null);
        builder.show();
    }

}
