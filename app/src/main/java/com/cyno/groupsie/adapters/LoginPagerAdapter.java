package com.cyno.groupsie.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.cyno.groupsie.activities.fragments.LoginPagerFragment;

/**
 * Created by hp on 02-09-2016.
 */
public class LoginPagerAdapter extends FragmentStatePagerAdapter {

    public LoginPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new LoginPagerFragment();
        Bundle mBundle = new Bundle();
        mBundle.putInt(LoginPagerFragment.KEY_POSITION, position);
        fragment.setArguments(mBundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return LoginPagerFragment.MAX_IMAGES;
    }
}
