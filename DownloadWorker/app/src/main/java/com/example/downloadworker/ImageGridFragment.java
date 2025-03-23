package com.example.downloadworker;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

// Brenna Pavlinchak
// AD2 - C202503
// ImageGridFragment

public class ImageGridFragment extends Fragment {

    private ImageAdapter imageAdapter;
    private final List<Uri> imageUris = new ArrayList<>();
    private ImageUpdateReceiver imageUpdateReceiver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_grid, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        imageAdapter = new ImageAdapter(imageUris, this::handleImageClick);
        recyclerView.setAdapter(imageAdapter);
        loadImages();

        // Register BroadcastReceiver with compatibility for API 24
        imageUpdateReceiver = new ImageUpdateReceiver(this);
        IntentFilter filter = new IntentFilter("com.example.downloadworker.IMAGE_UPDATED");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // API 26+ (Android 8.0 and above)
            requireContext().registerReceiver(imageUpdateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            // API 24 and 25 (Android 7.0 and 7.1)
            requireContext().registerReceiver(imageUpdateReceiver, filter);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requireContext().unregisterReceiver(imageUpdateReceiver);
    }

    public void loadImages() {
        int startPosition = imageUris.size();
        imageUris.clear();

        File storageDir = requireContext().getDir("images", Context.MODE_PRIVATE);
        File[] files = storageDir.listFiles();
        if (files != null) {
            for (File file : files) {
                Uri uri = androidx.core.content.FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.downloadworker.fileprovider",
                        file
                );
                imageUris.add(uri);
            }
        }

        int itemCount = imageUris.size();
        if (startPosition > 0) {
            imageAdapter.notifyItemRangeRemoved(0, startPosition);
        }
        if (itemCount > 0) {
            imageAdapter.notifyItemRangeInserted(0, itemCount);
        }
    }

    private void handleImageClick(Uri uri) {
        Intent viewIntent = new Intent(Intent.ACTION_VIEW);
        viewIntent.setDataAndType(uri, "image/*");
        viewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(viewIntent);
    }
}