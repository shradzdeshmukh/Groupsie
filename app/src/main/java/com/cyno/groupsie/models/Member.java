package com.cyno.groupsie.models;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by hp on 10-09-2016.
 */
public class Member {

    private static final String F_TABLE = "Member";
    private static final String C_USER_ID = "user_id";
    private static final String C_ALBUM_ID = "album_id";
    private String userId;
    private String albumId;

    public Member() {

    }

    public Member(String userID, String albumID) {
        this.userId = userID;
        this.albumId = albumID;
    }

    public static void writeMember(Member member) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(F_TABLE).child(member.getUserId() + member.getAlbumId()).child(C_USER_ID).setValue(member.getUserId());
        mDatabase.child(F_TABLE).child(member.getUserId() + member.getAlbumId()).child(C_ALBUM_ID).setValue(member.getAlbumId());
    }

    public static Member getMember(DataSnapshot members) {
        Member member = new Member();
        member.setUserId(members.child(C_USER_ID).getValue().toString());
        member.setAlbumId(members.child(C_ALBUM_ID).getValue().toString());
        Log.d("Member data", "member = " + member.toString());
        return member;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    @Override
    public String toString() {
        return "Member{" +
                "userId='" + userId + '\'' +
                ", albumId='" + albumId + '\'' +
                '}';
    }
}
