package com.cyno.groupsie.activities.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cyno.groupsie.R;
import com.cyno.groupsie.adapters.FriendListAdapter;
import com.cyno.groupsie.constatnsAndUtils.AlbumListDecorator;
import com.cyno.groupsie.database.MemberTable;
import com.cyno.groupsie.models.FBFriend;

import java.util.ArrayList;

/**
 * Created by hp on 12-10-2016.
 */
public class AlbumMembersFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {


    public static final java.lang.String KEY_ALBUM_ID = "id";
    private static final int LOADER_ID_MEMBERS = 1111;

    private ArrayList<FBFriend> alFriendList = new ArrayList<>();
    private String albumId;
    private FriendListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.albumId = getArguments().getString(KEY_ALBUM_ID, "");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_members, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rvMembers = (RecyclerView) view.findViewById(R.id.rv_album_members);
        rvMembers.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvMembers.addItemDecoration(new AlbumListDecorator(getActivity()));


        adapter = new FriendListAdapter(getActivity(), alFriendList, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        rvMembers.setAdapter(adapter);

        getLoaderManager().initLoader(LOADER_ID_MEMBERS, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), MemberTable.CONTENT_URI, null,
                MemberTable.COL_ALBUM_ID + " = ? ", new String[]{albumId}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        getAllMembers(data);
        adapter.notifyDataSetChanged();
    }

    private void getAllMembers(Cursor data) {
        while (data.moveToNext())
            alFriendList.add(FBFriend.getFriend(data.getString(data.getColumnIndex(MemberTable.COL_USER_ID)), getActivity()));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
