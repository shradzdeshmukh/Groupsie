package com.cyno.groupsie.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.cyno.groupsie.activities.LoginActivity;
import com.cyno.groupsie.database.MemberTable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by hp on 10-09-2016.
 */
public class Member {

    private static final String F_TABLE = "Member";
    private static final String C_USER_ID = "user_id";
    private static final String C_ALBUM_ID = "album_id";
    private static final String C_IS_REQUEST_ACCEPTED = "is_req_accepted";


    private String userId;
    private String albumId;
    private boolean isRequestAccepted;

    public Member() {

    }

    public Member(String userID, String albumID, boolean isRequestAccepted) {
        this.userId = userID;
        this.albumId = albumID;
        this.isRequestAccepted = isRequestAccepted;
    }

    public static void writeMember(Member member) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(F_TABLE).child(member.getUserId() + member.getAlbumId()).child(C_USER_ID).setValue(member.getUserId());
        mDatabase.child(F_TABLE).child(member.getUserId() + member.getAlbumId()).child(C_ALBUM_ID).setValue(member.getAlbumId());
        mDatabase.child(F_TABLE).child(member.getUserId() + member.getAlbumId()).child(C_IS_REQUEST_ACCEPTED).setValue(member.isRequestAccepted());
    }

    public static Member getMemberAndStoreLocally(DataSnapshot members, Context context) {
        Member member = new Member();
        member.setUserId(members.child(C_USER_ID).getValue().toString());
        member.setAlbumId(members.child(C_ALBUM_ID).getValue().toString());
        member.setRequestAccepted((Boolean) members.child(C_IS_REQUEST_ACCEPTED).getValue());
        Log.d("user id", "user id" + LoginActivity.getCurrentUser(context).getUserId());
        if (!member.isRequestAccepted() && member.getUserId().equals(LoginActivity.getCurrentUser(context).getUserId()))
            Member.insertInDB(context, member);
        Log.d("Member data", "member = " + member.toString());
        return member;
    }

    public static void insertInDB(Context context, Member member) {
        ContentValues values = new ContentValues();
        values.put(MemberTable.COL_ALBUM_ID, member.getAlbumId());
        values.put(MemberTable.COL_USER_ID, member.getUserId());
        values.put(MemberTable.COL_IS_REQ_ACCEPTED, member.isRequestAccepted());
        int count = context.getContentResolver().update(MemberTable.CONTENT_URI, values, MemberTable.COL_USER_ID
                        + " = ? AND " + MemberTable.COL_ALBUM_ID + " = ? ",
                new String[]{member.getUserId(), member.getAlbumId()});
        if (count == 0)
            context.getContentResolver().insert(MemberTable.CONTENT_URI, values);
    }

    public static Member getMember(Cursor cursor) {
        Member member = new Member();
        member.setAlbumId(cursor.getString(cursor.getColumnIndex(MemberTable.COL_ALBUM_ID)));
        member.setUserId(cursor.getString(cursor.getColumnIndex(MemberTable.COL_USER_ID)));
        member.setRequestAccepted(cursor.getString(cursor.getColumnIndex(MemberTable.COL_IS_REQ_ACCEPTED)).equalsIgnoreCase("1"));
        return member;
    }

    public static ArrayList<Member> getAllMembers(Cursor cursor) {
        ArrayList<Member> alMembers = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                alMembers.add(getMember(cursor));
            }
            cursor.close();
        }
        return alMembers;
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

    public boolean isRequestAccepted() {
        return isRequestAccepted;
    }

    public void setRequestAccepted(boolean requestAccepted) {
        isRequestAccepted = requestAccepted;
    }

    @Override
    public String toString() {
        return "Member{" +
                "userId='" + userId + '\'' +
                ", albumId='" + albumId + '\'' +
                '}';
    }
}
