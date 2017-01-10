package com.cyno.groupsie.constatnsAndUtils;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.cyno.groupsie.Interfaces.IProgressListner;
import com.cyno.groupsie.database.AlbumTable;
import com.cyno.groupsie.models.Album;
import com.cyno.groupsie.models.Member;
import com.cyno.groupsie.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by hp on 11-10-2016.
 */
public class AlbumUtils {

    private static ValueEventListener valueListnerAlbums;
    private static ValueEventListener valueListnerMembers;

    public static String getAlbumId() {
        return "XYZ" + System.currentTimeMillis();
    }

    public static int getGrade() {
        return 123;
    }

    public static void getAllAlbums(final Context context, User user, final IProgressListner progressListner) {

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("/Member/");
        progressListner.showProgress();
        final Query query = mDatabase.orderByChild("user_id").equalTo(user.getUserId());
        valueListnerMembers = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("data", dataSnapshot.toString());
                ArrayList<Member> memberList = getAllMembers(dataSnapshot, context);
                getAllAlbums(context, memberList);
                mDatabase.removeEventListener(valueListnerMembers);
                query.removeEventListener(valueListnerMembers);
                progressListner.onDataLoaded();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mDatabase.removeEventListener(valueListnerMembers);
                query.removeEventListener(valueListnerMembers);
                progressListner.onDataLoaded();

            }
        };
        query.addListenerForSingleValueEvent(valueListnerMembers);
    }

    private static void getAllAlbums(final Context context, ArrayList<Member> memberList) {
        ArrayList<String> albumIds = new ArrayList<>();
        for (Member member : memberList) {
            albumIds.add(member.getAlbumId());
        }
        for (String albumId : albumIds) {
            getAlbum(context, albumId);
        }

    }

    public static void getAlbum(final Context context, String albumId) {
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("/Album/" + albumId);
        valueListnerAlbums = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Album.getAlbumData(context, dataSnapshot);
                mDatabase.removeEventListener(valueListnerAlbums);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mDatabase.removeEventListener(valueListnerAlbums);
            }
        };
        mDatabase.addListenerForSingleValueEvent(valueListnerAlbums);
    }


    private static ArrayList<Member> getAllMembers(DataSnapshot dataSnapshot, Context context) {
        ArrayList<Member> memberArrayList = new ArrayList<>();
        for (DataSnapshot members : dataSnapshot.getChildren()) {
            memberArrayList.add(Member.getMemberAndStoreLocally(members, context));
        }
        return memberArrayList;
    }

    public static Album getLocalAlbum(Context context, String albumId) {
        Cursor cursor = context.getContentResolver().query(AlbumTable.CONTENT_URI, null,
                AlbumTable.COL_ALBUM_UNIQUE_ID + " = ? ", new String[]{albumId}, null);
        Album album = null;
        if (cursor != null) {
            if (cursor.moveToNext()) {
                album = Album.getAlbum(cursor);
            }
            cursor.close();
        }
        return album;
    }

    public static String getAlbumName(Context context, String albumId) {
        return getLocalAlbum(context, albumId).getAlbumName();
    }
}
