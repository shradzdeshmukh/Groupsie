package com.cyno.groupsie.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.cyno.groupsie.R;
import com.cyno.groupsie.adapters.AlbumDetailPagerAdapter;
import com.cyno.groupsie.constatnsAndUtils.ImageUtils;
import com.cyno.groupsie.constatnsAndUtils.PhotoUtils;
import com.cyno.groupsie.models.Album;
import com.cyno.groupsie.models.Photo;

import java.io.File;
import java.io.IOException;

public class AlbumDetailActivity extends AppCompatActivity {

    public static final String KEY_ALBUM = "album";
    private Album currentAlbum;
    private File currentImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        currentAlbum = getIntent().getParcelableExtra(KEY_ALBUM);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeImage();
            }
        });


        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void takeImage() {
        try {
            currentImageFile = ImageUtils.dispatchTakePictureIntent(this, PhotoUtils.getPhotoId(currentAlbum.getAlbumId()));
            Log.d("imagepath", "" + currentImageFile.getName());
//            ImageUtils.putCurrentImageName(currentImageName, this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        AlbumDetailPagerAdapter adapter = new AlbumDetailPagerAdapter(getSupportFragmentManager(), this, currentAlbum);
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ImageUtils.REQUEST_TAKE_PHOTO) {
            Log.d("file", "after image " + currentImageFile.getName());
            Photo photo = new Photo();
            photo.setPhotoId(currentImageFile.getName());
            photo.setAlbumId(currentAlbum.getAlbumId());
            photo.setPhotoLocalUrl(currentImageFile.getPath());
            photo.setFileSize(currentImageFile.length());
            Log.d("path", currentImageFile.getPath());
            Log.d("path", currentImageFile.getName());
            ImageUtils.compressImage(this, currentImageFile);
            PhotoUtils.setProminentColor(this, photo);
            Photo.uploadAndInsert(this, photo);


        } else {
            Log.d("pic", "nopic");
        }
    }

    public Album getCurrentAlbum() {
        return currentAlbum;
    }
}
