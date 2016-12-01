package com.cyno.groupsie.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cyno.groupsie.Interfaces.ILogoutListner;
import com.cyno.groupsie.R;
import com.cyno.groupsie.database.AlbumTable;
import com.cyno.groupsie.database.FbFriendsTable;
import com.cyno.groupsie.database.PhotosTable;
import com.cyno.groupsie.models.User;
import com.cyno.groupsie.sync.SyncUtils;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by hp on 27-08-2016.
 */
public abstract class BaseActivity extends AppCompatActivity
        implements FirebaseAuth.AuthStateListener
        , ILogoutListner {

    private static final String TAG = "base activity";
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_PROFILE_PIC = "prf_pic";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "email";

    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        SyncUtils.setSyncAccount(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (!(this instanceof LoginActivity)) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.base_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.logout:
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                break;
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuth != null) {
            mAuth.removeAuthStateListener(this);
        }
    }


    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    protected FirebaseAuth getAuth() {
        return mAuth;
    }

    protected void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
    }

    protected User getCurrentUser(){
        User user = new User();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        user.setUserId(pref.getString(KEY_USER_ID , ""));
        user.setProfilePicUrl(pref.getString(KEY_USER_PROFILE_PIC, ""));
        user.setUsername(pref.getString(KEY_USER_NAME, ""));
        user.setEmail(pref.getString(KEY_USER_EMAIL , ""));
        return user;
    }

    protected void setCurrentUser(User user){
        PreferenceManager.getDefaultSharedPreferences(this).edit().
                putString(KEY_USER_ID , user.getUserId()).
                putString(KEY_USER_PROFILE_PIC , user.getProfilePicUrl()).
                putString(KEY_USER_NAME , user.getUsername()).
                putString(KEY_USER_EMAIL , user.getEmail()).commit();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null && !(this instanceof LoginActivity)) {
            Log.d("auth changed", "logout");
            Intent mIntent = new Intent(this, LoginActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mIntent);
        }
    }

    @Override
    public void onLogout() {

        clearDb();
        LoginActivity.setLoggedIn(this, false);
    }

    private void clearDb() {
        getContentResolver().delete(AlbumTable.CONTENT_URI, null, null);
        getContentResolver().delete(FbFriendsTable.CONTENT_URI, null, null);
        getContentResolver().delete(PhotosTable.CONTENT_URI, null, null);
    }
}
