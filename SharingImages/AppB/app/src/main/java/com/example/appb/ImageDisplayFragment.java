package com.example.appb;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import java.util.ArrayList;

// Brenna Pavlinchak
// AD2 - C202503
// ImageDisplayFragment

public class ImageDisplayFragment extends Fragment
{
    private static final String TAG = "ImageDisplayFragment";
    private GridView gridView;
    private ArrayList<Uri> imageUris;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView called");
        View view = inflater.inflate(R.layout.fragment_image_display, container, false);
        gridView = view.findViewById(R.id.image_grid);

        imageUris = new ArrayList<>();
        ImageGridAdapter adapter = new ImageGridAdapter();
        gridView.setAdapter(adapter);

        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result ->
                {
                    Log.d(TAG, "pickImageLauncher callback triggered with resultCode: " + result.getResultCode());

                    if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null)
                    {
                        Uri uri = result.getData().getData();
                        if (uri != null)
                        {
                            Log.d(TAG, "Received URI from App A: " + uri);
                            try
                            {
                                if (getContext() != null)
                                {
                                    java.io.InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
                                    if (inputStream != null)
                                    {
                                        inputStream.close();
                                        Log.d(TAG, "URI is accessible: " + uri);
                                        imageUris.add(uri); // Add the URI to the list
                                        adapter.notifyDataSetChanged(); // Refresh the GridView
                                    }
                                    else
                                    {
                                        Log.e(TAG, "Failed to open InputStream for URI: " + uri);
                                        Toast.makeText(getContext(), "Cannot access the selected image", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else
                                {
                                    Log.e(TAG, "Context is null, cannot load URI: " + uri);
                                    Toast.makeText(getContext(), "Cannot load the selected image", Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "URI is not accessible: " + uri, e);
                                if (getContext() != null)
                                {
                                    Toast.makeText(getContext(), "Cannot access the selected image", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        else
                        {
                            Log.w(TAG, "No URI received from App A");
                            if (getContext() != null)
                            {
                                Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else
                    {
                        Log.w(TAG, "Image pick failed or was canceled, resultCode: " + result.getResultCode());
                        if (getContext() != null)
                        {
                            Toast.makeText(getContext(), "Image selection canceled", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        requireActivity().addMenuProvider(new MenuProvider()
        {
            @Override
            public void onCreateMenu(@NonNull android.view.Menu menu, @NonNull android.view.MenuInflater menuInflater)
            {
                Log.d(TAG, "Inflating menu");
                menuInflater.inflate(R.menu.menu_main, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull android.view.MenuItem menuItem)
            {
                Log.d(TAG, "Menu item selected: " + menuItem.getItemId());

                if (menuItem.getItemId() == R.id.action_pick_image)
                {
                    Intent pickIntent = new Intent(Intent.ACTION_PICK);
                    pickIntent.setType("image/*");
                    pickIntent.setPackage("com.example.appa");
                    try
                    {
                        Log.d(TAG, "Launching pickImageLauncher");
                        pickImageLauncher.launch(pickIntent);
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, "Failed to launch image picker", e);
                        if (getContext() != null)
                        {
                            Toast.makeText(getContext(), "Failed to launch image picker", Toast.LENGTH_SHORT).show();
                        }
                    }
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        return view;
    }

    private class ImageGridAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return imageUris.size();
        }

        @Override
        public Object getItem(int position)
        {
            return imageUris.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ImageView imageView;
            if (convertView == null)
            {
                imageView = new ImageView(getContext());
                imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            else
            {
                imageView = (ImageView) convertView;
            }

            Uri imageUri = imageUris.get(position);
            Glide.with(ImageDisplayFragment.this)
                    .load(imageUri)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(android.R.drawable.ic_menu_close_clear_cancel)
                    .listener(new RequestListener<>()
                    {
                        @SuppressWarnings("unused")
                        @Override
                        public boolean onLoadFailed(@Nullable com.bumptech.glide.load.engine.GlideException e, @NonNull Object model, @NonNull com.bumptech.glide.request.target.Target<Drawable> target, boolean isFirstResource)
                        {
                            Log.e(TAG, "Failed to load image URI: " + imageUri, e);
                            return false;
                        }

                        @SuppressWarnings("unused")
                        @Override
                        public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, @NonNull com.bumptech.glide.request.target.Target<Drawable> target, @NonNull com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource)
                        {
                            Log.d(TAG, "Successfully loaded image URI: " + imageUri);
                            return false;
                        }
                    })
                    .into(imageView);

            return imageView;
        }
    }
}