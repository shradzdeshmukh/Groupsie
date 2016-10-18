package com.cyno.groupsie.constatnsAndUtils;

import android.content.Context;
import android.util.Log;

import com.cyno.groupsie.models.Album;
import com.cyno.groupsie.models.Member;
import com.google.firebase.auth.FirebaseUser;
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

    public static void getAllAlbums(final Context context, FirebaseUser user) {

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("/Member/");

        final Query query = mDatabase.orderByChild("user_id").equalTo(user.getUid());
        valueListnerMembers = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("data", dataSnapshot.toString());
                ArrayList<Member> memberList = getAllMembers(dataSnapshot);
                getAllAlbums(context, memberList);
                mDatabase.removeEventListener(valueListnerMembers);
                query.removeEventListener(valueListnerMembers);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mDatabase.removeEventListener(valueListnerMembers);
                query.removeEventListener(valueListnerMembers);

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

    }


    private static ArrayList<Member> getAllMembers(DataSnapshot dataSnapshot) {
        ArrayList<Member> memberArrayList = new ArrayList<>();
        for (DataSnapshot members : dataSnapshot.getChildren()) {
            memberArrayList.add(Member.getMember(members));

        }
        return memberArrayList;
    }
}
