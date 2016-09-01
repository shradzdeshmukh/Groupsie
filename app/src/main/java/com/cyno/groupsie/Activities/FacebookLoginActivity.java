/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cyno.groupsie.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cyno.groupsie.Models.User;
import com.cyno.groupsie.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

/**
 * Demonstrate Firebase Authentication using a Facebook access token.
 */
public class FacebookLoginActivity extends BaseActivity implements
        View.OnClickListener {

    private static final String TAG = "FacebookLogin";
    private static final String KEY_FB_UID = "FBUID";


    private CallbackManager mCallbackManager;
    private FirebaseUser user;
    private String url;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_facebook);
        findViewById(R.id.button_facebook_signout).setOnClickListener(this);

        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.button_facebook_login);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                PreferenceManager.getDefaultSharedPreferences(FacebookLoginActivity.this)
                        .edit().putString(KEY_FB_UID , loginResult.getAccessToken().getUserId()).commit();
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                updateUI(null);
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                updateUI(null);
            }
        });

        findViewById(R.id.bt_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
    }

    private void saveData() {
        User u = new User();
        u.writeNewUser(user.getUid() , user.getDisplayName() , user.getEmail() , url);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }



    private void updateUI(FirebaseUser user) {
        this.user = user;
//        String url = "https://scontent.xx.fbcdn.net/v/t1.0-1/p400x400/11998801_10153639880824802_4670712918900224263_n.jpg?oh=ef4948c7977af1835c58317e6cd52d7d&oe=57D4127E";
        url = "https://graph.facebook.com/"+ PreferenceManager.getDefaultSharedPreferences(this).getString(KEY_FB_UID , "")+"/picture?type=large&width=500&height=500";
        if (user == null) {
            findViewById(R.id.view_fb_profile).setVisibility(View.GONE);
            findViewById(R.id.button_facebook_signout).setVisibility(View.GONE);
            findViewById(R.id.button_facebook_login).setVisibility(View.VISIBLE);

        }else {
            ((TextView) findViewById(R.id.tv_facebook_name)).setText(user.getDisplayName());
            Log.d(FacebookLoginActivity.TAG , "photo = "+String.valueOf(user.getPhotoUrl()).replace("50","500"));
            findViewById(R.id.view_fb_profile).setVisibility(View.VISIBLE);
            Picasso.with(this).load(url).error(R.mipmap.ic_launcher)
                    .into(((ImageView) findViewById(R.id.iv_facebook_dp)));
            user.getDisplayName();
            findViewById(R.id.button_facebook_signout).setVisibility(View.VISIBLE);
            findViewById(R.id.button_facebook_login).setVisibility(View.GONE);

            Log.d("providers" , user.getProviderData().toString());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_facebook_signout:
                signOut();
                break;
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        showProgressDialog();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        getAuth().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(FacebookLoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // User is signed in
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }
        updateUI(user);
    }

    @Override
    public void onLogout() {
        updateUI(null);
    }
}
