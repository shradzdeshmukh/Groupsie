package com.cyno.groupsie.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cyno.groupsie.R;
import com.cyno.groupsie.models.Album;

import java.util.ArrayList;


/**
 * Created by hp on 11-10-2016.
 */
public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.ViewHolder> {
    private final Context context;
    private final View.OnClickListener clickListner;
    private ArrayList<Album> alAlbumList;

    public AlbumListAdapter(Context context, ArrayList<Album> alAlbumList, View.OnClickListener clickListner) {
        super();
        this.context = context;
        this.alAlbumList = alAlbumList;
        this.clickListner = clickListner;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_album_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.txtAlbumName.setText(alAlbumList.get(position).getAlbumName());

        holder.rootView.setTag(R.string.tag_album_root, alAlbumList.get(position));
        holder.rootView.setOnClickListener(clickListner);
    }

    @Override
    public int getItemCount() {
        return alAlbumList.size();
    }

    public void refreshList(Cursor cursor) {
        alAlbumList.clear();
        while (cursor.moveToNext()) {
            alAlbumList.add(Album.getAlbum(cursor));
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final View rootView;
        private TextView txtAlbumName;


        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            txtAlbumName = (TextView) itemView.findViewById(R.id.txt_album_name);
        }
    }
}
