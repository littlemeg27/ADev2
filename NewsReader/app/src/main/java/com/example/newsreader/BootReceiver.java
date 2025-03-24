package com.example.newsreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;

// Brenna Pavlinchak
// AD2 - C202503
// BootReceiver

public class BootReceiver extends BroadcastReceiver
{
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
        {
            Log.d(TAG, "Boot completed, scheduling periodic work");
            schedulePeriodicWork(context);
        }
    }

    private void schedulePeriodicWork(Context context)
    {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                DownloadWorker.class,
                15, TimeUnit.MINUTES
        )
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "download_work",
                WorkManager.getInstance(context).getWorkInfosForUniqueWork("download_work").isDone() ?
                        androidx.work.ExistingPeriodicWorkPolicy.KEEP :
                        androidx.work.ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
        );
    }
}
