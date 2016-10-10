package com.cyno.groupsie.models;

import android.content.Context;

import com.cyno.groupsie.constatnsAndUtils.AppUtils;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by user on 9/6/16.
 */

@IgnoreExtraProperties
public class User {

    private static final String F_TABLE_NAME = "Users";
    private static final String C_NAME = "name";
    private static final String C_EMAIL = "email";
    private static final String C_DP_URL = "pic";

    private String username;
    private String userId;
    private String email;
    private String profilePicUrl;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userId,String username, String email , String profilePicUrl) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.profilePicUrl = profilePicUrl;
    }

    public User(FirebaseUser currentUser , Context context) {
        this.userId = currentUser.getUid();
        this.email = currentUser.getEmail();
        this.username= currentUser.getDisplayName();
        this.profilePicUrl= AppUtils.getDPUrl(context);
    }

    public static void writeUser(User user) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(F_TABLE_NAME).child(user.getUserId()).child(C_NAME).setValue(user.getUsername());
        mDatabase.child(F_TABLE_NAME).child(user.getUserId()).child(C_EMAIL).setValue(user.getEmail());
        mDatabase.child(F_TABLE_NAME).child(user.getUserId()).child(C_DP_URL).setValue(user.getProfilePicUrl());

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }
}