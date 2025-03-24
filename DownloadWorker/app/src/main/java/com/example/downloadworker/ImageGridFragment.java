package com.example.downloadworker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

// Brenna Pavlinchak
// AD2 - C202503
// ImageGridFragment.java

public class ImageGridFragment extends Fragment
{
    private ImageAdapter imageAdapter;
    private final List<Uri> imageUris = new ArrayList<>();
    private ImageUpdateReceiver imageUpdateReceiver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_image_grid, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        imageAdapter = new ImageAdapter(imageUris, this::handleImageClick);
        recyclerView.setAdapter(imageAdapter);
        loadImages();

        imageUpdateReceiver = new ImageUpdateReceiver(this);
        IntentFilter filter = new IntentFilter("com.example.downloadworker.IMAGE_UPDATED");
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(imageUpdateReceiver, filter);

        return view;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(imageUpdateReceiver);
    }

    public void loadImages()
    {
        Log.d("ImageGridFragment", "Loading images...");
        List<Uri> newImageUris = new ArrayList<>();
        File storageDir = requireContext().getFilesDir();
        File imagesDir = new File(storageDir, "images");
        Log.d("ImageGridFragment", "Storage directory: " + imagesDir.getAbsolutePath());
        File[] files = imagesDir.listFiles();

        if (files != null)
        {
            Log.d("ImageGridFragment", "Found " + files.length + " files");

            for (File file : files)
            {
                Log.d("ImageGridFragment", "Found file: " + file.getAbsolutePath());
                Uri uri = androidx.core.content.FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.downloadworker.fileprovider",
                        file
                );
                newImageUris.add(uri);
            }
        }
        else
        {
            Log.d("ImageGridFragment", "No files found in storage directory");
        }

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new UriDiffCallback(imageUris, newImageUris));
        imageUris.clear();
        imageUris.addAll(newImageUris);
        diffResult.dispatchUpdatesTo(imageAdapter);
    }

    private void handleImageClick(Uri uri)
    {
        Intent viewIntent = new Intent(Intent.ACTION_VIEW);
        viewIntent.setDataAndType(uri, "image/*");
        viewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(viewIntent);
    }

    private static class UriDiffCallback extends DiffUtil.Callback
    {
        private final List<Uri> oldList;
        private final List<Uri> newList;

        UriDiffCallback(List<Uri> oldList, List<Uri> newList)
        {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize()
        {
            return oldList.size();
        }

        @Override
        public int getNewListSize()
        {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition)
        {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition)
        {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }

    private static class ImageUpdateReceiver extends BroadcastReceiver
    {
        private final ImageGridFragment fragment;

        ImageUpdateReceiver(ImageGridFragment fragment)
        {
            this.fragment = fragment;
        }

        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.d("ImageUpdateReceiver", "Received broadcast: " + intent.getAction());

            if (fragment != null)
            {
                fragment.loadImages();
            }
        }
    }
}