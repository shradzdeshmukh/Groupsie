package com.cyno.groupsie.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by hp on 10-09-2016.
 */
public class Member {

    private static final String F_TABLE = "Member";
    private static final String C_USER_ID = "user_id";
    private static final String C_ALBUM_ID = "album_id";
    private User user;
    private Album album;

    public Member(User user, Album album) {
        this.user = user;
        this.album = album;
    }

    public static void writeMember(Member member) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(F_TABLE).child(member.getUser().getUserId() + member.getAlbum().getAlbumId()).child(C_USER_ID).setValue(member.getUser().getUserId());
        mDatabase.child(F_TABLE).child(member.getUser().getUserId() + member.getAlbum().getAlbumId()).child(C_ALBUM_ID).setValue(member.getAlbum().getAlbumId());
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }


}
