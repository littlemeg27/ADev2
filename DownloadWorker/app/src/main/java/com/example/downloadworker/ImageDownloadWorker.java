package com.example.downloadworker;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// Brenna Pavlinchak
// AD2 - C202503
// ImageDownloadWorker.java

public class ImageDownloadWorker extends Worker
{
    private static final String TAG = "ImageDownloadWorker";
    private static final String URL_BASE = "https://i.imgur.com/";
    private static final String[] IMAGES =
            {
            "Df9sV7x.jpg", "nqnegVs.jpg", "JDCG1tP.jpg", "tUvlwvB.jpg",
            "2bTEbC5.jpg", "Jnqn9NJ.jpg", "xd2M3FF.jpg", "atWe0me.jpg",
            "UJROzhm.jpg", "4lEPonM.jpg", "vxvaFmR.jpg", "NDPbJfV.jpg",
            "ZPdoCbQ.jpg", "SX6hzar.jpg", "YDNldPb.jpg", "iy1FvVh.jpg",
            "P0RMPwI.jpg", "lKrCKtM.jpg", "aBcDeFg.jpg", "hIjKlMn.jpg",
            "oPqRsTu.jpg", "vWxYzAb.jpg", "cDeFgHi.jpg", "jKlMnOp.jpg"
    };

    public ImageDownloadWorker(@NonNull Context context, @NonNull WorkerParameters params)
    {
        super(context, params);
        Log.d(TAG, "ImageDownloadWorker initialized");
    }

    @NonNull
    @Override
    public Result doWork()
    {
        Log.d(TAG, "Starting doWork() to download images");
        File storageDir = new File(getApplicationContext().getFilesDir(), "images");

        if (!storageDir.exists())
        {
            storageDir.mkdirs();
        }
        Log.d(TAG, "Storage directory: " + storageDir.getAbsolutePath());

        for (String imageName : IMAGES)
        {
            Log.d(TAG, "Processing image: " + imageName);
            File imageFile = new File(storageDir, imageName);
            Log.d(TAG, "Image file path: " + imageFile.getAbsolutePath());

            if (!imageFile.exists())
            {
                Log.d(TAG, "Image does not exist, attempting to download: " + imageName);
                boolean downloaded = downloadImage(URL_BASE + imageName, imageFile);

                if (downloaded)
                {
                    Log.d(TAG, "Successfully downloaded image: " + imageName);
                    sendBroadcast();
                }
                else
                {
                    Log.w(TAG, "Failed to download image: " + imageName);
                }
            }
            else
            {
                Log.d(TAG, "Image already exists, skipping download: " + imageName);
            }
        }
        Log.d(TAG, "Finished downloading images");
        return Result.success();
    }

    private boolean downloadImage(String imageUrl, File outputFile)
    {
        Log.d(TAG, "Downloading image from URL: " + imageUrl);
        try
        {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36");
            connection.connect();

            int responseCode = connection.getResponseCode();
            Log.d(TAG, "HTTP Response Code: " + responseCode);

            if (responseCode != HttpURLConnection.HTTP_OK)
            {
                Log.e(TAG, "Failed to download image, HTTP response code: " + responseCode);
                return false;
            }

            InputStream input = connection.getInputStream();
            FileOutputStream output = new FileOutputStream(outputFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            long totalBytes = 0;
            Log.d(TAG, "Starting to read input stream...");

            while ((bytesRead = input.read(buffer)) != -1)
            {
                output.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
            }

            Log.d(TAG, "Total bytes downloaded: " + totalBytes);
            output.close();
            input.close();
            connection.disconnect();
            Log.d(TAG, "Download completed successfully");
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
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("com.example.downloadworker.IMAGE_UPDATED"));
        Log.d(TAG, "Sent broadcast: IMAGE_UPDATED");
    }
}