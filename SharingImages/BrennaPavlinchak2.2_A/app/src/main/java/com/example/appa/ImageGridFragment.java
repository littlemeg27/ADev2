package com.example.appa;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

// Brenna Pavlinchak
// AD2 - C202503
// ImageGridFragment

public class ImageGridFragment extends Fragment
{
    private ArrayList<Uri> imageUris = new ArrayList<>();
    private ImageAdapter imageAdapter;
    private ActivityResultLauncher<Intent> takePictureLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_image_grid, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        imageAdapter = new ImageAdapter(imageUris, this::handleImageClick, this::handleDelete);
        recyclerView.setAdapter(imageAdapter);
        loadImages();

        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result ->
                {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK)
                    {
                        int previousSize = imageUris.size();
                        imageUris.clear();
                        loadImages();
                        int newSize = imageUris.size();
                        if (newSize > previousSize)
                        {
                            imageAdapter.notifyItemRangeInserted(previousSize, newSize - previousSize);
                        }
                        else
                        {
                            imageAdapter.notifyDataSetChanged();
                        }
                    }
                });

        requireActivity().addMenuProvider(new MenuProvider()
        {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater)
            {
                menuInflater.inflate(R.menu.menu_main, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem)
            {
                if (menuItem.getItemId() == R.id.action_take_picture)
                {
                    dispatchTakePictureIntent();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        return view;
    }

    private void dispatchTakePictureIntent()
    {
        File photoFile = createImageFile();
        Uri photoUri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.appa.fileprovider",
                photoFile
        );
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        takePictureLauncher.launch(takePictureIntent);
    }

    private File createImageFile()
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = requireContext().getDir("images", Context.MODE_PRIVATE);
        return new File(storageDir, "IMG_" + timeStamp + ".jpg");
    }

    private void loadImages()
    {
        File storageDir = requireContext().getDir("images", Context.MODE_PRIVATE);
        File[] files = storageDir.listFiles();
        if (files != null)
        {
            for (File file : files)
            {
                Uri uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.appa.fileprovider",
                        file
                );
                imageUris.add(uri);
            }
            imageAdapter.notifyDataSetChanged();
        }
    }

    private void handleImageClick(Uri uri)
    {
        String action = requireActivity().getIntent().getAction();

        if (Intent.ACTION_PICK.equals(action))
        {
            Intent resultIntent = new Intent();
            resultIntent.setData(uri);
            resultIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            requireActivity().setResult(android.app.Activity.RESULT_OK, resultIntent);
            requireActivity().finish();
        }
        else
        {
            Intent viewIntent = new Intent(Intent.ACTION_VIEW);
            viewIntent.setDataAndType(uri, "image/*");
            viewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(viewIntent);
        }
    }

    private void handleDelete(Uri uri)
    {
        String path = uri.getPath();

        if (path != null)
        {
            File file = new File(path);

            if (file.exists())
            {
                boolean deleted = file.delete();

                if (!deleted)
                {
                    Log.w("ImageGridFragment", "Failed to delete file: " + uri);
                }
            }
        }
    }
}