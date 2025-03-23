package com.example.downloadworker;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// Brenna Pavlinchak
// AD2 - C202503
// ImageDownloadWorker

public class ImageDownloadWorker extends Worker
{

    private static final String TAG = "ImageDownloadWorker";
    private static final String URL_BASE = "https://i.imgur.com/";
    private static final String[] IMAGES = {
            "Df9sV7x.jpg", "nqnegVs.jpg", "JDCG1tP.jpg",
            "tUvlwvB.jpg", "2bTEbC5.jpg", "Jnqn9NJ.jpg",
            "xd2M3FF.jpg", "atWe0me.jpg", "UJROzhm.jpg",
            "4lEPonM.jpg", "vxvaFmR.jpg", "NDPbJfV.jpg",
            "ZPdoCbQ.jpg", "SX6hzar.jpg", "YDNldPb.jpg",
            "iy1FvVh.jpg", "2bTEbC5.jpg", "P0RMPwI.jpg",
            "lKrCKtM.jpg"
    };

    public ImageDownloadWorker(@NonNull Context context, @NonNull WorkerParameters params)
    {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork()
    {
        for (String imageName : IMAGES)
        {
            File imageFile = new File(getApplicationContext().getDir("images", Context.MODE_PRIVATE), imageName);
            if (!imageFile.exists())
            {
                boolean downloaded = downloadImage(URL_BASE + imageName, imageFile);
                if (downloaded)
                {
                    Log.d(TAG, "Downloaded image: " + imageName);
                    sendBroadcast();
                }
                else
                {
                    Log.w(TAG, "Failed to download image: " + imageName);
                }
            }
            else
            {
                Log.d(TAG, "Image already exists: " + imageName);
            }
        }
        return Result.success();
    }

    private boolean downloadImage(String imageUrl, File outputFile)
    {
        try
        {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            FileOutputStream output = new FileOutputStream(outputFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            output.close();
            input.close();
            connection.disconnect();
            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error downloading image: " + imageUrl, e);
            return false;
        }
    }

    private void sendBroadcast()
    {
        Intent intent = new Intent("com.example.downloadworker.IMAGE_UPDATED");
        getApplicationContext().sendBroadcast(intent);
        Log.d(TAG, "Sent broadcast: IMAGE_UPDATED");
    }
}