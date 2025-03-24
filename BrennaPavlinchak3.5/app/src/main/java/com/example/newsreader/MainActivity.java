package com.example.newsreader;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;

// Brenna Pavlinchak
// AD2 - C202503
// MainActivity

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted ->
                {
                    if (isGranted)
                    {
                        Log.d(TAG, "POST_NOTIFICATIONS permission granted");
                        schedulePeriodicWork();
                    }
                    else
                    {
                        Log.w(TAG, "POST_NOTIFICATIONS permission denied");
                    }
                });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED)
            {
                Log.d(TAG, "Requesting POST_NOTIFICATIONS permission");
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
            else
            {
                Log.d(TAG, "POST_NOTIFICATIONS permission already granted");
                schedulePeriodicWork();
            }
        }
        else
        {
            schedulePeriodicWork();
        }

        if (savedInstanceState == null)
        {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new NewsListFragment());
            transaction.commit();
        }
    }

    private void schedulePeriodicWork()
    {
        Log.d(TAG, "Scheduling periodic work");
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                DownloadWorker.class,
                15, TimeUnit.MINUTES
        )
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "download_work",
                WorkManager.getInstance(this).getWorkInfosForUniqueWork("download_work").isDone() ?
                        androidx.work.ExistingPeriodicWorkPolicy.KEEP :
                        androidx.work.ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                workRequest
        );
        Log.d(TAG, "Periodic work scheduled");
    }
}