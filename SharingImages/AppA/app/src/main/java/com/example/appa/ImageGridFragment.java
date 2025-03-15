package com.example.appa;

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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ImageGridFragment extends Fragment
{
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private ArrayList<Uri> imageUris = new ArrayList<>();
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_grid, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        imageAdapter = new ImageAdapter(imageUris, this::handleImageClick, this::handleDelete);
        recyclerView.setAdapter(imageAdapter);
        loadImages();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_take_picture) {
            dispatchTakePictureIntent();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void dispatchTakePictureIntent() {
        File photoFile = createImageFile();
        Uri photoUri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.appa.fileprovider",
                photoFile
        );
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File storageDir = requireContext().getDir("images", MODE_PRIVATE);
        return new File(storageDir, "IMG_" + timeStamp + ".jpg");
    }

    private void loadImages() {
        File storageDir = requireContext().getDir("images", MODE_PRIVATE);
        File[] files = storageDir.listFiles();
        if (files != null) {
            for (File file : files) {
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

    private void handleImageClick(Uri uri) {
        String action = requireActivity().getIntent().getAction();
        if (Intent.ACTION_PICK.equals(action)) {
            // Return the URI to App B
            Intent resultIntent = new Intent();
            resultIntent.setData(uri);
            resultIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            requireActivity().setResult(RESULT_OK, resultIntent);
            requireActivity().finish();
        } else {
            // Open the image in the gallery app
            Intent viewIntent = new Intent(Intent.ACTION_VIEW);
            viewIntent.setDataAndType(uri, "image/*");
            viewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(viewIntent);
        }
    }

    private void handleDelete(Uri uri) {
        File file = new File(uri.getPath());
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageUris.clear();
            loadImages();
        }
    }
}