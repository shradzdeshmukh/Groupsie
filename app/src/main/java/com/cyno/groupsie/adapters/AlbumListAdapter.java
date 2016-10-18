package com.cyno.groupsie.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.cyno.groupsie.R;
import com.cyno.groupsie.database.AlbumTable;
import com.cyno.groupsie.models.Album;

/**
 * Created by hp on 11-10-2016.
 */
public class AlbumListAdapter extends CursorAdapter {
    public AlbumListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = View.inflate(context, R.layout.item_album_list, null);
        ViewHolder holder = new ViewHolder();
        holder.txtAlbumName = (TextView) view.findViewById(R.id.txt_album_name);
        view.setTag(R.string.viewholder, holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag(R.string.viewholder);
        holder.txtAlbumName.setText(cursor.getString(cursor.getColumnIndex(AlbumTable.COL_ALBUM_NAME)));
        view.setTag(R.string.tab_album, Album.getAlbum(cursor));
    }

    static class ViewHolder {
        private TextView txtAlbumName;
    }
}
