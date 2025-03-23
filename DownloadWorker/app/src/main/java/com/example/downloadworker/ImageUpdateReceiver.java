package com.example.downloadworker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// Brenna Pavlinchak
// AD2 - C202503
// ImageUpdateReceiver

public class ImageUpdateReceiver extends BroadcastReceiver
{
    private final ImageGridFragment fragment;

    public ImageUpdateReceiver(ImageGridFragment fragment)
    {
        this.fragment = fragment;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (fragment != null)
        {
            fragment.loadImages();
        }
    }
}
