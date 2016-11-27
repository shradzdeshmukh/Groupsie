package com.cyno.groupsie.constatnsAndUtils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.cyno.groupsie.models.Photo;

import java.io.File;

/**
 * Created by user on 17/8/16.
 */
public class AmazonUtils {

    private static final String COMPLETED = "COMPLETED";

    public static String AMAZON_DIR = "https://s3.amazonaws.com/dinogroupsie/";
    private static ClientConfiguration connectionConfig;
    private static TransferUtility sTransferUtility;
    private static AmazonS3Client sS3Client;
    private static CognitoCachingCredentialsProvider sCredProvider;

    public static final void uploadImage(final Context context, final String amazonDir, final Photo photo) {
        File mFile = new File(Uri.parse(photo.getPhotoLocalUrl()).getPath());
        Log.d("amazon" , "path " +photo.getPhotoLocalUrl());
        Log.d("amazon" , mFile.getTotalSpace()+"");

        mFile = ImageUtils.compressToUploadImage(context, mFile);

        TransferUtility transferUtility = getTransferUtility(context);
        TransferObserver obs = transferUtility.upload(Constants.AMAZON_S3_BUCKET, amazonDir, mFile);
        obs.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.d("amazon" , "state schanged= " + state.name());
                Log.d("amazon", "id= " + id);
                if (state.name().equals(COMPLETED)) {
                    Photo mLocalPhoto = photo;
                    mLocalPhoto.setPhotoServerUrl(AMAZON_DIR + amazonDir);
                    Log.d("amazon", "url = " + mLocalPhoto.getPhotoServerUrl());
                    Photo.syncToFirebase(context, mLocalPhoto);
                    mLocalPhoto.setState(Photo.state.STATE_ON_SERVER.ordinal());
                    Photo.insertOrUpdate(context, mLocalPhoto);
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                Log.d("amazon" , "current byytes = " + bytesCurrent);
                Photo mLocalPhoto = photo;
                mLocalPhoto.setState(Photo.state.STATE_UPLOADING.ordinal());
                mLocalPhoto.setProgressSize((int) bytesCurrent);
                Photo.insertOrUpdate(context, mLocalPhoto);

            }

            @Override
            public void onError(int id, Exception ex) {
                Log.d("amazon" , "error = " + ex.getLocalizedMessage());
                Photo mLocalPhoto = photo;
                mLocalPhoto.setState(Photo.state.STATE_ERROR_UPLOADING.ordinal());
                Photo.insertOrUpdate(context, mLocalPhoto);

            }
        });
    }

    private static  ClientConfiguration getConnectionConfig(){
        if(connectionConfig == null) {
            connectionConfig = new ClientConfiguration();
            connectionConfig.setSocketTimeout(Constants.SOCKET_TIME_OUT);
            connectionConfig.setConnectionTimeout(Constants.SOCKET_TIME_OUT);
        }
        return connectionConfig;
    }


    /**
     * Gets an instance of the TransferUtility which is constructed using the
     * given Context
     *
     * @param context
     * @return a TransferUtility instance
     */
    public static TransferUtility getTransferUtility(Context context) {
        if (sTransferUtility == null) {
            sTransferUtility = new TransferUtility(getS3Client(context.getApplicationContext()),
                    context.getApplicationContext());
        }

        return sTransferUtility;
    }

    /**
     * Gets an instance of a S3 client which is constructed using the given
     * Context.
     *
     * @param context An Context instance.
     * @return A default S3 client.
     */
    public static AmazonS3Client getS3Client(Context context) {
        if (sS3Client == null) {
            sS3Client = new AmazonS3Client(getCredProvider(context.getApplicationContext()), getConnectionConfig());
        }
        return sS3Client;
    }

    /**
     * Gets an instance of CognitoCachingCredentialsProvider which is
     * constructed using the given Context.
     *
     * @param context An Context instance.
     * @return A default credential provider.
     */
    private static CognitoCachingCredentialsProvider getCredProvider(Context context) {
        if (sCredProvider == null) {
            sCredProvider =  new CognitoCachingCredentialsProvider(
                    context.getApplicationContext(),
                    "us-west-2:cacee17e-fd44-429d-ab75-2234beb8c0a2", // Identity Pool ID
                    Regions.US_WEST_2 // Region
            );
        }
        return sCredProvider;
    }


}
