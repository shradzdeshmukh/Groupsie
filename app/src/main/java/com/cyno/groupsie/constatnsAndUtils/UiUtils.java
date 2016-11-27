package com.cyno.groupsie.constatnsAndUtils;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cyno.groupsie.R;

/**
 * Created by hp on 06-11-2016.
 */
public class UiUtils {

    public static void showEmptyView(View rootView, int emptyText, int imageId) {
        TextView textView = (TextView) rootView.findViewById(R.id.txt_empty_view);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.iv_empty_view);
        textView.setText(emptyText);
        imageView.setImageResource(imageId);
        rootView.findViewById(R.id.empty_layout).setVisibility(View.VISIBLE);
    }

    public static void hideEmptyView(View rootView) {
        rootView.findViewById(R.id.empty_layout).setVisibility(View.GONE);
    }
}
