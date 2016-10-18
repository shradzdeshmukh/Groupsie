package com.cyno.groupsie.activities.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.GridView;

import com.cyno.groupsie.R;
import com.cyno.groupsie.activities.AlbumDetailActivity;
import com.cyno.groupsie.adapters.PhotosGridAdapter;
import com.cyno.groupsie.database.PhotosTable;

/**
 * Created by hp on 12-10-2016.
 */
public class AlbumPhotosFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 100;
    private PhotosGridAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_album_photos, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GridView gvPhotos = (GridView) view.findViewById(R.id.gv_photos);
        adapter = new PhotosGridAdapter(getActivity(), null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        gvPhotos.setAdapter(adapter);
        getLoaderManager().initLoader(LOADER_ID, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), PhotosTable.CONTENT_URI, null,
                PhotosTable.COL_ALBUM_UNIQUE_ID + " = ? ",
                new String[]{((AlbumDetailActivity) getActivity()).getCurrentAlbum().getAlbumId()}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("pics", "count = " + data.getCount());
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
