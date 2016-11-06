package com.cyno.groupsie.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.cyno.groupsie.R;
import com.cyno.groupsie.database.PhotosTable;
import com.squareup.picasso.Picasso;

/**
 * Created by hp on 11-10-2016.
 */
public class PhotosGridAdapter extends CursorAdapter {
    private final Context context;

    public PhotosGridAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = View.inflate(context, R.layout.item_photos_grid, null);
        ViewHolder holder = new ViewHolder();
        holder.imgPhoto = (ImageView) view.findViewById(R.id.img_photo);
        view.setTag(R.string.viewholder, holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag(R.string.viewholder);
        Log.d("adapter", "file:///" + cursor.getString(cursor.getColumnIndex(PhotosTable.COL_PHOTO_LOCAL_URL)) + "");
        String imageFile = "file:///" + cursor.getString(cursor.getColumnIndex(PhotosTable.COL_PHOTO_LOCAL_URL));
        Picasso.with(context)
                .load(imageFile)
                .resize(480, 640)
                .centerInside()
                .into(holder.imgPhoto);

    }

    static class ViewHolder {
        private ImageView imgPhoto;
    }
}
