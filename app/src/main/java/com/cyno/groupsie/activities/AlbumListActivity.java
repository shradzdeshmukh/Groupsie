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
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.cyno.groupsie.R;
import com.cyno.groupsie.adapters.AlbumListAdapter;
import com.cyno.groupsie.constatnsAndUtils.AlbumUtils;
import com.cyno.groupsie.database.AlbumTable;
import com.cyno.groupsie.models.Album;
import com.cyno.groupsie.models.Member;
import com.cyno.groupsie.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AlbumListActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private static final int LOADER_ID = 100;
    private AlbumListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewAlbumDialog();
            }
        });

        ListView lvAlbums = (ListView) findViewById(R.id.lst_albums);
        adapter = new AlbumListAdapter(this, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        lvAlbums.setAdapter(adapter);
        lvAlbums.setOnItemClickListener(this);

        getLoaderManager().initLoader(LOADER_ID, null, this);

        AlbumUtils.getAllAlbums(this, getAuth().getCurrentUser());

    }

    private void testData() {
        User user = new User(FirebaseAuth.getInstance().getCurrentUser(), AlbumListActivity.this);
        Album album = new Album(user.getUserId() + System.currentTimeMillis(), "Test album", "test url ", 100, System.currentTimeMillis());

        User.writeUser(user);
//        Album.writeAlbum(album);

        Member member = new Member(user.getUserId(), album.getAlbumId());
        Member.writeMember(member);


    }

    @Override
    public void onLogout() {

    }


    private void showNewAlbumDialog() {
        View rootView = View.inflate(this, R.layout.create_album, null);
        final EditText edtCreateAlbum = (EditText) rootView.findViewById(R.id.edt_create_album);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.create_album_title)
                .setView(rootView)
                .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createNewAlbum(getAuth().getCurrentUser(), edtCreateAlbum.getText().toString());
                    }
                }).setNegativeButton(R.string.cancel, null).show();
    }

    private void createNewAlbum(FirebaseUser user, String albumName) {
        Album album = new Album();
        album.setAlbumName(albumName);
        album.setAlbumId(AlbumUtils.getAlbumId());
        album.setCreateDate(System.currentTimeMillis());
        album.setGrade(AlbumUtils.getGrade());
        Album.storeAndWriteAlbum(user.getUid(), album, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, AlbumTable.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, AlbumDetailActivity.class);
        intent.putExtra(AlbumDetailActivity.KEY_ALBUM, (Album) view.getTag(R.string.tab_album));
        startActivity(intent);
    }
}
