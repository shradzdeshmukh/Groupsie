package com.cyno.groupsie.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cyno.groupsie.R;
import com.cyno.groupsie.constatnsAndUtils.AlbumUtils;
import com.cyno.groupsie.constatnsAndUtils.DialogHelper;
import com.cyno.groupsie.models.Album;
import com.cyno.groupsie.models.Member;

import java.util.ArrayList;


/**
 * Created by hp on 11-10-2016.
 */
public class RequestsListAdapter extends RecyclerView.Adapter<RequestsListAdapter.ViewHolder> {
    private final Context context;
    private final View.OnClickListener clickListner;
    private ArrayList<Member> alMemberList;

    public RequestsListAdapter(Context context, ArrayList<Member> alAlbumList, View.OnClickListener clickListner) {
        super();
        this.context = context;
        this.alMemberList = alAlbumList;
        this.clickListner = clickListner;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_requests, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.txtAlbumName.setText(AlbumUtils.getAlbumName(context, alMemberList.get(position).getAlbumId()));

        holder.rootView.setTag(R.string.tag_requests, alMemberList.get(position));
        holder.vUncheck.setTag(R.string.tag_requests, alMemberList.get(position));
        holder.vCheck.setTag(R.string.tag_requests, alMemberList.get(position));
        holder.rootView.setOnClickListener(clickListner);
        holder.vCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Member member = (Member) v.getTag(R.string.tag_requests);
                member.setRequestAccepted(true);
                Member.writeMember(member);
                Member.insertInDB(context, member);
                Album album = AlbumUtils.getLocalAlbum(context, member.getAlbumId());
                album.setRequestAccepted(true);
                Album.insertOrUpdate(context, album);
            }
        });

        holder.vUncheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelper.ShowDialog(context, R.string.title_reject_album, R.string.msg_reject_album, android.R.string.yes, android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO
                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return alMemberList.size();
    }

    public void refreshList(Cursor cursor) {
        alMemberList.clear();
        while (cursor.moveToNext()) {
            alMemberList.add(Member.getMember(cursor));
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final View rootView;
        private TextView txtAlbumName;
        private View vCheck, vUncheck;


        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            txtAlbumName = (TextView) itemView.findViewById(R.id.txt_album_name);
            vCheck = itemView.findViewById(R.id.img_check);
            vUncheck = itemView.findViewById(R.id.img_un_check);
        }
    }
}
