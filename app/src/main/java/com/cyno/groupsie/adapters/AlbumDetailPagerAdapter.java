package com.cyno.groupsie.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.cyno.groupsie.R;
import com.cyno.groupsie.activities.AlbumDetailActivity;
import com.cyno.groupsie.activities.fragments.AlbumMembersFragment;
import com.cyno.groupsie.activities.fragments.AlbumPhotosFragment;
import com.cyno.groupsie.models.Album;

/**
 * Created by hp on 02-09-2016.
 */
public class AlbumDetailPagerAdapter extends FragmentStatePagerAdapter {

    private final Context context;
    private final Album album;

    public AlbumDetailPagerAdapter(FragmentManager fm, Context context, Album currentAlbum) {
        super(fm);
        this.context = context;
        this.album = currentAlbum;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new AlbumPhotosFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(AlbumDetailActivity.KEY_ALBUM, album);
                fragment.setArguments(bundle);
                break;
            case 1:
                fragment = new AlbumMembersFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.photos);
            case 1:
                return context.getString(R.string.members);
        }
        return null;
    }
}
