package com.cyno.groupsie.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cyno.groupsie.R;
import com.cyno.groupsie.constatnsAndUtils.PicassoCircleTransform;
import com.cyno.groupsie.models.FBFriend;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by hp on 11-10-2016.
 */
public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {
    private final Context context;
    private final View.OnClickListener clickListner;
    private ArrayList<FBFriend> alFriendList;

    public FriendListAdapter(Context context, ArrayList<FBFriend> alFriendList, View.OnClickListener clickListner) {
        super();
        this.context = context;
        this.alFriendList = alFriendList;
        this.clickListner = clickListner;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*View rootView = LayoutInflater.from(context).inflate(R.layout.item_add_friend_list, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rootView.setLayoutParams(lp);
        return new ViewHolder(rootView);*/
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_add_friend_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.txtFriendName.setText(alFriendList.get(position).getName());

        Picasso.with(context).load(alFriendList.get(position).getProfilePicUrl()).
                transform(new PicassoCircleTransform()).into(holder.ivFriend);

        holder.rootView.setTag(R.string.tag_add_friend, alFriendList.get(position));
        holder.rootView.setOnClickListener(clickListner);

        if (alFriendList.get(position).isSelected())
            holder.rootView.setBackgroundColor(ContextCompat.getColor(context, R.color.frnd_selected));
        else
            holder.rootView.setBackgroundColor(ContextCompat.getColor(context, R.color.background));


    }

    @Override
    public int getItemCount() {
        return alFriendList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        private final View rootView;
        private TextView txtFriendName;
        private ImageView ivFriend;


        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            txtFriendName = (TextView) itemView.findViewById(R.id.txt_friend_name);
            ivFriend = (ImageView) itemView.findViewById(R.id.iv_friend);
        }
    }


}
