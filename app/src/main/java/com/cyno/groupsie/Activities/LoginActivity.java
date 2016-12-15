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

package com.cyno.groupsie.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.cyno.groupsie.R;
import com.cyno.groupsie.adapters.LoginPagerAdapter;
import com.cyno.groupsie.constatnsAndUtils.Constants;
import com.cyno.groupsie.constatnsAndUtils.FBUtils;
import com.cyno.groupsie.models.User;
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

import me.relex.circleindicator.CircleIndicator;

/**
 * Demonstrate Firebase Authentication using a Facebook access token.
 */
public class LoginActivity extends BaseActivity {

    private static final String TAG = "FacebookLogin";
    private static final String KEY_FB_UID = "FBUID";


    private CallbackManager mCallbackManager;

    public static void setLoggedIn(Context context , boolean isLoggedIn){
        PreferenceManager.getDefaultSharedPreferences(context).edit().
                putBoolean(Constants.KEY_IS_LOGGED_IN, isLoggedIn).commit();
    }

    public static boolean isLoggedIn(Context context ){
        return PreferenceManager.getDefaultSharedPreferences(context).
                getBoolean(Constants.KEY_IS_LOGGED_IN, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_facebook);
        setViewPager();

        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.button_facebook_login);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.setReadPermissions("user_friends", "public_profile");

        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this)
                        .edit().putString(KEY_FB_UID , loginResult.getAccessToken().getUserId()).commit();
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });

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
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
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
        super.onAuthStateChanged(firebaseAuth);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String fbUid = PreferenceManager.getDefaultSharedPreferences(this).getString(KEY_FB_UID,"");
        String url = FBUtils.getProfilePicUrl(fbUid);
        PreferenceManager.getDefaultSharedPreferences(this).edit().
                putString(Constants.USER_DP_URL, url).commit();
        if (user != null) {
            // User is signed in
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            User usr = new User(fbUid, user.getDisplayName(), user.getEmail(), url);
            User.writeUser(usr);
            FBUtils.GetFriendList(this , fbUid);
            setCurrentUser(usr);
            startNextActivity();
        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }

    }



    private void setViewPager(){
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new LoginPagerAdapter(getSupportFragmentManager()));
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator_default);
        indicator.setViewPager(viewPager);
    }

    private void startNextActivity(){
        startActivity(new Intent(this , AlbumListActivity.class));
        finish();
    }
}
