package com.cyno.groupsie.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.cyno.groupsie.BuildConfig;
import com.cyno.groupsie.R;
import com.cyno.groupsie.constatnsAndUtils.AppUtils;
import com.cyno.groupsie.constatnsAndUtils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity {


    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private OnCompleteListener<Void> listner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listner = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SplashActivity.this, "Fetch Succeeded " +
                                    (int) mFirebaseRemoteConfig.getLong(Constants.KEY_APP_VERSION),
                            Toast.LENGTH_SHORT).show();
                    AppUtils.putVersionNumber((int) mFirebaseRemoteConfig.getLong(Constants.KEY_APP_VERSION), SplashActivity.this);
                    mFirebaseRemoteConfig.activateFetched();
                    if (AppUtils.isVersionAllowed(SplashActivity.this)) {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    } else
                        AppUtils.ShowUpgradeDialog(SplashActivity.this);
                } else {
                    Toast.makeText(SplashActivity.this, "Fetch Failed",
                            Toast.LENGTH_SHORT).show();
                }

            }
        };

        setContentView(R.layout.activity_splah_screen);
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        mFirebaseRemoteConfig.fetch(Constants.CACHE_EXPIRATION)
                .addOnCompleteListener(listner);


    }

    @Override
    protected void onStop() {
        super.onStop();
        listner = null;
    }
}
