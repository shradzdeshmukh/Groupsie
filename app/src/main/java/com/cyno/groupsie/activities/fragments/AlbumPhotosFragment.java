package com.cyno.groupsie.activities.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cyno.groupsie.Interfaces.IProgressListner;
import com.cyno.groupsie.R;
import com.cyno.groupsie.activities.AlbumDetailActivity;
import com.cyno.groupsie.adapters.PhotosGridAdapter;
import com.cyno.groupsie.constatnsAndUtils.PhotoUtils;
import com.cyno.groupsie.constatnsAndUtils.UiUtils;
import com.cyno.groupsie.database.PhotosTable;
import com.cyno.groupsie.models.Photo;

import java.util.ArrayList;

/**
 * Created by hp on 12-10-2016.
 */
public class AlbumPhotosFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener, IProgressListner {

    private static final int LOADER_ID = 100;
    private PhotosGridAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

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

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        RecyclerView rvPhotos = (RecyclerView) view.findViewById(R.id.rv_photos);
        rvPhotos.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        adapter = new PhotosGridAdapter(getActivity(), new ArrayList<Photo>());


        rvPhotos.setAdapter(adapter);
        getLoaderManager().initLoader(LOADER_ID, null, this);

        PhotoUtils.getAllPhotos(getActivity(), ((AlbumDetailActivity) getActivity()).getCurrentAlbum().getAlbumId(), this);
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
        adapter.refreshList(data);
        if (data.getCount() == 0) {
            UiUtils.showEmptyView(getView(), R.string.empty_list, R.drawable.com_facebook_profile_picture_blank_portrait);
        } else {
            UiUtils.hideEmptyView(getView());
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onRefresh() {
        PhotoUtils.getAllPhotos(getActivity(), ((AlbumDetailActivity) getActivity()).getCurrentAlbum().getAlbumId(), this);

    }

    @Override
    public void onDataLoaded() {
        swipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 500);
    }

    @Override
    public void showProgress() {
        swipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        }, 500);
    }
}
