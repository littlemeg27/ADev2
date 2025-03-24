package com.example.newsreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UIRefreshReceiver extends BroadcastReceiver {
    private static final String TAG = "NewsListFragment";
    private final NewsListFragment fragment;

    public UIRefreshReceiver(NewsListFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received UI refresh broadcast");
        fragment.refreshArticles();
    }
}
