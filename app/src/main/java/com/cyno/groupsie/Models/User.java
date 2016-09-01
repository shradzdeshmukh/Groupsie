package com.cyno.groupsie.Models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by user on 9/6/16.
 */

@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public String profilePicUrl;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email , String profilePicUrl) {
        this.username = username;
        this.email = email;
        this.profilePicUrl = profilePicUrl;
    }

    public void writeNewUser(String userId, String name, String email , String profilePicUrl) {
        User user = new User(name, email , profilePicUrl);
        mDatabase.child("users").child(userId).setValue(user);
    }
}