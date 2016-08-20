package com.cyno.groupsie;

import android.content.Context;
import android.util.Log;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

import java.io.File;

/**
 * Created by user on 17/8/16.
 */
public class AmazonUtils {

    private static ClientConfiguration connectionConfig;
    private static TransferUtility sTransferUtility;
    private static AmazonS3Client sS3Client;
    private static CognitoCachingCredentialsProvider sCredProvider;

    public static final void uploadImage(Context context ,String amazonDir, String imagePath){

/*
        AmazonS3Client s3Client = new AmazonS3Client(
                new BasicAWSCredentials(Constants.AMAZON_ACCESS_KEY, Constants.AMAZON_SECRET), getConnectionConfig());
        PutObjectRequest por = new PutObjectRequest(Constants.AMAZON_S3_BUCKET,
                amazonDir, new java.io.File(imagePath));

        por.setCannedAcl(CannedAccessControlList.PublicRead);

        PutObjectResult result = s3Client.putObject(por);
        Log.e("amazon", "result = " + result.getETag()
                + " MD5 " + result.getContentMd5());
*/


        TransferUtility transferUtility = getTransferUtility(context);
        TransferObserver obs = transferUtility.upload(Constants.AMAZON_S3_BUCKET, "test", new File(imagePath));
        obs.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                Log.d("amazon" , "current byytes = " + bytesCurrent);
            }

            @Override
            public void onError(int id, Exception ex) {

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
            sS3Client = new AmazonS3Client(getCredProvider(context.getApplicationContext()));
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
            sCredProvider = new CognitoCachingCredentialsProvider(
                    context.getApplicationContext(),
                    "us-west-2_obVRfGcEy",
                    Regions.AP_SOUTH_1);
        }
        return sCredProvider;
    }

}
