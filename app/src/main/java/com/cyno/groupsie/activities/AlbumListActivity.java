package com.cyno.groupsie.activities;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.cyno.groupsie.Interfaces.IProgressListner;
import com.cyno.groupsie.R;
import com.cyno.groupsie.adapters.AlbumListAdapter;
import com.cyno.groupsie.adapters.FriendListAdapter;
import com.cyno.groupsie.adapters.RequestsListAdapter;
import com.cyno.groupsie.constatnsAndUtils.AlbumListDecorator;
import com.cyno.groupsie.constatnsAndUtils.AlbumUtils;
import com.cyno.groupsie.constatnsAndUtils.UiUtils;
import com.cyno.groupsie.database.AlbumTable;
import com.cyno.groupsie.database.FbFriendsTable;
import com.cyno.groupsie.database.MemberTable;
import com.cyno.groupsie.models.Album;
import com.cyno.groupsie.models.FBFriend;
import com.cyno.groupsie.models.Member;
import com.cyno.groupsie.models.User;

import java.util.ArrayList;

public class AlbumListActivity extends BaseActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener,
        IProgressListner, View.OnClickListener {

    private static final int LOADER_ID_ALBUMS = 100;
    private static final int LOADER_ID_FRIENDS = 200;
    private static final int LOADER_ID_REQUESTS = 300;

    private AlbumListAdapter adapterAlbums;
    private RequestsListAdapter adapterRequests;
    private ArrayList<Album> alAlbumList = new ArrayList<>();
    private ArrayList<FBFriend> friendlist = new ArrayList<>();
    private ArrayList<Member> memberlist = new ArrayList<>();
    private FriendListAdapter friendListAdapter;
    private View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_list);
        rootView = findViewById(R.id.root_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewAlbumDialog();
            }
        });


        RecyclerView rvAlbums = (RecyclerView) findViewById(R.id.rv_albums);
        rvAlbums.setLayoutManager(new LinearLayoutManager(this));
        rvAlbums.addItemDecoration(new AlbumListDecorator(this));
        adapterAlbums = new AlbumListAdapter(this, alAlbumList, this);
        rvAlbums.setAdapter(adapterAlbums);


        RecyclerView rvRequests = (RecyclerView) findViewById(R.id.rv_requests);
        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        rvRequests.addItemDecoration(new AlbumListDecorator(this));
        adapterRequests = new RequestsListAdapter(this, memberlist, this);
        rvRequests.setAdapter(adapterRequests);

        getLoaderManager().initLoader(LOADER_ID_ALBUMS, null, this);
        getLoaderManager().initLoader(LOADER_ID_FRIENDS, null, this);
        getLoaderManager().initLoader(LOADER_ID_REQUESTS, null, this);

        AlbumUtils.getAllAlbums(this, getCurrentUser(this), this);

    }



    @Override
    public void onLogout() {

    }


    private void showNewAlbumDialog() {
        View rootView = LayoutInflater.from(this).inflate(R.layout.create_album, null);
        final EditText edtCreateAlbum = (EditText) rootView.findViewById(R.id.edt_create_album);
        final RecyclerView rvFriendList = (RecyclerView) rootView.findViewById(R.id.rv_add_friends_dialog);
        rvFriendList.setLayoutManager(new LinearLayoutManager(this));
        friendListAdapter = new FriendListAdapter(this, friendlist, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FBFriend friend = (FBFriend) v.getTag(R.string.tag_add_friend);
                friend.setSelected(!friend.isSelected());
                friendListAdapter.notifyDataSetChanged();

            }
        });

        rvFriendList.setAdapter(friendListAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.create_album_title)
                .setView(rootView)
                .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createNewAlbum(getCurrentUser(AlbumListActivity.this), edtCreateAlbum.getText().toString());
                    }
                }).setNegativeButton(R.string.cancel, null).show();
    }

    private void createNewAlbum(User user, String albumName) {
        Album album = new Album();
        album.setAlbumName(albumName);
        album.setAlbumId(AlbumUtils.getAlbumId());
        album.setCreateDate(System.currentTimeMillis());
        album.setGrade(AlbumUtils.getGrade());
        album.setRequestAccepted(true);
        Album.storeAndWriteAlbum(user.getUserId(), album, this);

        for (FBFriend friend : friendlist) {
            if (friend.isSelected()) {
                Member member = new Member(friend.getId(), album.getAlbumId());
                member.setRequestAccepted(true);
                Member.insertInDB(this, member);
                Member.writeMember(member);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ID_ALBUMS:
                return new CursorLoader(this, AlbumTable.CONTENT_URI, null, AlbumTable.COL_IS_REQ_ACCEPTED + " = ? ", new String[]{String.valueOf(1)}, null);
            case LOADER_ID_FRIENDS:
                return new CursorLoader(this, FbFriendsTable.CONTENT_URI, null, null, null, null);
            case LOADER_ID_REQUESTS:
                return new CursorLoader(this, MemberTable.CONTENT_URI, null,
                        MemberTable.COL_IS_REQ_ACCEPTED + " = ? ", new String[]{"0"}, null);

        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case LOADER_ID_ALBUMS:
                adapterAlbums.refreshList(data);
                break;
            case LOADER_ID_FRIENDS:
                friendlist = FBFriend.getAllFriends(data);
                break;
            case LOADER_ID_REQUESTS:
                adapterRequests.refreshList(data);
                Log.d("requests", data.getCount() + "");
                break;
        }

        if (adapterAlbums.getItemCount() == 0 && adapterRequests.getItemCount() == 0) {
            UiUtils.showEmptyView(rootView, R.string.empty_list, R.drawable.com_facebook_profile_picture_blank_portrait);
        } else {
            UiUtils.hideEmptyView(rootView);
        }
    }

     /*   Cursor cursor = getContentResolver().query(FbFriendsTable.CONTENT_URI,null,null,null,null);
        Log.d("cursor","cursor" +cursor.getCount());*/


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onDataLoaded() {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, AlbumDetailActivity.class);
        intent.putExtra(AlbumDetailActivity.KEY_ALBUM, (Album) view.getTag(R.string.tag_album_root));
        startActivity(intent);

    }
}
