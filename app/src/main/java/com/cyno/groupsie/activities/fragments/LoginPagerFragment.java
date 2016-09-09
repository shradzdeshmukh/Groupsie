package com.cyno.groupsie.activities.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cyno.groupsie.R;

/**
 * Created by hp on 02-09-2016.
 */
public class LoginPagerFragment extends Fragment {

    public static final String KEY_POSITION = "pos";
    public static final int MAX_IMAGES = 3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.frag_login_pager, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imgLogin = (ImageView) view.findViewById(R.id.img_login_pager);
        switch (getArguments().getInt(KEY_POSITION, -1)) {
            case 0:
                imgLogin.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
                break;
            case 1:
                imgLogin.setImageResource(R.drawable.com_facebook_tooltip_black_xout);
                break;

            case 2:
                imgLogin.setImageResource(R.drawable.com_facebook_button_like_icon_selected);
                break;

        }
    }
}
