package com.cyno.groupsie.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cyno.groupsie.Interfaces.ILogoutListner;
import com.cyno.groupsie.R;
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

    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        SyncUtils.setSyncAccount(this);
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
}
