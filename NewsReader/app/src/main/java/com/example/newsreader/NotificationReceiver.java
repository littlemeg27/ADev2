package com.example.newsreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class NotificationReceiver extends BroadcastReceiver
{
    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if ("com.example.newsreader.ACTION_SAVE_ARTICLE".equals(intent.getAction()))
        {
            String title = intent.getStringExtra("article_title");
            String url = intent.getStringExtra("article_url");
            Log.d(TAG, "Received save action for article: " + title);

            Data inputData = new Data.Builder()
                    .putString("article_title", title)
                    .putString("article_url", url)
                    .build();

            OneTimeWorkRequest saveRequest = new OneTimeWorkRequest.Builder(SaveWorker.class)
                    .setInputData(inputData)
                    .build();

            WorkManager.getInstance(context).enqueue(saveRequest);
        }
    }
}