package com.cyno.groupsie.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.cyno.groupsie.R;
import com.cyno.groupsie.constatnsAndUtils.AmazonUtils;
import com.cyno.groupsie.constatnsAndUtils.PhotoUtils;
import com.cyno.groupsie.models.Photo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;


/**
 * Created by hp on 11-10-2016.
 */
public class PhotosGridAdapter extends RecyclerView.Adapter<PhotosGridAdapter.ViewHolder> {
    private static final int MAX = 400;
    private static final int MIN = 400;

    private final Context context;
    private ArrayList<Photo> alPhotoList;
    private Random mRandom = new Random();

    public PhotosGridAdapter(Context context, ArrayList<Photo> alPhotoList) {
        super();
        this.context = context;
        this.alPhotoList = alPhotoList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(context, R.layout.item_photos_grid, null));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.d("adapter", "file:///" + alPhotoList.get(position).getPhotoLocalUrl());
        String imageFile = "file:///" + alPhotoList.get(position).getPhotoLocalUrl();
        holder.imgPhoto.setBackgroundColor(alPhotoList.get(position).getProminentColor());


        holder.imgErrDownload.setVisibility(View.GONE);
        holder.imgErrUpload.setVisibility(View.GONE);
        holder.prgDownload.setVisibility(View.GONE);
        holder.prgUpload.setVisibility(View.GONE);

 /*       int size = getRandomSize();
        holder.rootView.getLayoutParams().height = size;
        holder.rootView.getLayoutParams().height = size;*/

        showImage(alPhotoList.get(position), holder);

        Photo.state state = Photo.state.values()[alPhotoList.get(position).getState()];
        Log.d("state", "state = " + state);
        switch (state) {
            case STATE_DOWNLOADED:
                break;
            case STATE_ERROR_DOWNLOADING:
                holder.imgErrDownload.setVisibility(View.VISIBLE);
                break;
            case STATE_ERROR_UPLOADING:
                holder.imgErrUpload.setVisibility(View.VISIBLE);
                break;
            case STATE_ON_SERVER:
                break;
            case STATE_UPLOADING:
                holder.prgUpload.setVisibility(View.VISIBLE);
                break;
            case STATE_DOWNLOADING:
                holder.prgDownload.setVisibility(View.VISIBLE);
                break;
        }


        holder.imgErrUpload.setTag(R.string.tag_photo, alPhotoList.get(position));
        holder.imgErrDownload.setTag(R.string.tag_photo, alPhotoList.get(position));


        holder.imgErrUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Photo photo = (Photo) v.getTag(R.string.tag_photo);
                AmazonUtils.uploadImage(context, PhotoUtils.getAmazonFolder(photo), photo);
            }
        });

        holder.imgErrDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Photo photo = (Photo) v.getTag(R.string.tag_photo);
                PhotoUtils.savePicToFile(photo, context);
            }
        });


    }

    @Override
    public int getItemCount() {
        return alPhotoList.size();
    }

    private void showImage(Photo photo, ViewHolder holder) {
       /* String url = null;
        if (TextUtils.isEmpty(photo.getPhotoLocalUrl())) {
            url = photo.getPhotoServerUrl();
            try {
                if (photo.getState() != Photo.state.STATE_DOWNLOADED.ordinal())
                    PhotoUtils.savePicToFile(photo, context);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            url = "file:///" + photo.getPhotoLocalUrl();*/
        int size = getRandomSize();
        holder.imgPhoto.getLayoutParams().height = size;
        Picasso.with(context)
                .load("file:///" + photo.getPhotoLocalUrl())
                .resize(200, size)
                .centerInside()
                .into(holder.imgPhoto);

    }

    public int getRandomSize() {
        return mRandom.nextInt((MAX - MIN) + MIN) + MIN;
    }

    public void refreshList(Cursor cursor) {
        alPhotoList.clear();
        while (cursor.moveToNext()) {
            alPhotoList.add(Photo.getPhoto(cursor));
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final View rootView;
        private ImageView imgPhoto;
        private ImageView imgErrUpload;
        private ImageView imgErrDownload;
        private ProgressBar prgUpload;
        private ProgressBar prgDownload;


        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            imgPhoto = (ImageView) itemView.findViewById(R.id.img_photo);
            imgErrDownload = (ImageView) itemView.findViewById(R.id.img_download_error);
            imgErrUpload = (ImageView) itemView.findViewById(R.id.img_upload_error);
            prgUpload = (ProgressBar) itemView.findViewById(R.id.prg_upload);
            prgDownload = (ProgressBar) itemView.findViewById(R.id.prg_download);

  /*        prgDownload = (RingProgressBar) itemView.findViewById(R.id.progress_bar_download);*/
        }
    }
}
