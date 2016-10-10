package com.cyno.groupsie.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.cyno.groupsie.R;
import com.cyno.groupsie.constatnsAndUtils.AlbumUtils;
import com.cyno.groupsie.models.Album;
import com.cyno.groupsie.models.Member;
import com.cyno.groupsie.models.User;
import com.google.firebase.auth.FirebaseAuth;

public class AlbumListActivity extends BaseActivity {

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
    }

    private void testData() {
        User user = new User(FirebaseAuth.getInstance().getCurrentUser(), AlbumListActivity.this);
        Album album = new Album(user.getUserId() + System.currentTimeMillis(), "Test album", "test url ", 100, System.currentTimeMillis());

        User.writeUser(user);
//        Album.writeAlbum(album);

        Member member = new Member(user, album);
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
                        createNewAlbum(edtCreateAlbum.getText().toString());
                    }
                }).setNegativeButton(R.string.cancel, null).show();
    }

    private void createNewAlbum(String albumName) {
        Album album = new Album();
        album.setAlbumName(albumName);
        album.setAlbumId(AlbumUtils.getAlbumId());
        album.setCreateDate(System.currentTimeMillis());
        album.setGrade(AlbumUtils.getGrade());
        Album.storeAndWriteAlbum(album, this);
    }
}
