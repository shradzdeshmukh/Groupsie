package com.cyno.groupsie.activities;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by hp on 11-10-2016.
 */
public class GroupsieApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(getApplicationContext());
    }
}
