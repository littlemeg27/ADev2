package com.example.appa;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

// Brenna Pavlinchak
// AD2 - C202503
// ImageGridFragment

public class ImageGridFragment extends Fragment
{
    private static final String TAG = "ImageGridFragment";
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private android.net.Uri photoUri;
    private ArrayList<android.net.Uri> imageUris;
    private ImageGridAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result ->
                {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK)
                    {
                        Log.d(TAG, "Photo captured successfully at URI: " + photoUri);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && photoUri != null && getActivity() != null)
                        {
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.Images.Media.IS_PENDING, 0);
                            int rowsUpdated = getActivity().getContentResolver().update(photoUri, values, null, null);
                            if (rowsUpdated > 0)
                            {
                                Log.d(TAG, "IS_PENDING flag cleared for URI: " + photoUri);
                            }
                            else
                            {
                                Log.e(TAG, "Failed to clear IS_PENDING flag for URI: " + photoUri);
                            }
                        }
                        refreshImageList();
                    }
                    else
                    {
                        Log.w(TAG, "Photo capture failed or was canceled");
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_image_grid, container, false);

        GridView gridView = view.findViewById(R.id.image_grid);
        imageUris = new ArrayList<>();

        refreshImageList();

        adapter = new ImageGridAdapter();
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((parent, view1, position, id) ->
        {
            if (getActivity() == null)
            {
                Log.e(TAG, "Fragment is not attached to an activity, cannot return result");
                return;
            }
            android.net.Uri selectedUri = imageUris.get(position);

            try
            {
                getActivity().getContentResolver().openInputStream(selectedUri).close();
            }
            catch (Exception e)
            {
                Log.e(TAG, "Selected URI is not accessible: " + selectedUri, e);
                Toast.makeText(getActivity(), "Cannot access the selected image", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent resultIntent = new Intent();
            resultIntent.setData(selectedUri);
            getActivity().setResult(android.app.Activity.RESULT_OK, resultIntent);
            getActivity().finish();
        });

        requireActivity().addMenuProvider(new MenuProvider()
        {
            @Override
            public void onCreateMenu(@NonNull android.view.Menu menu, @NonNull android.view.MenuInflater menuInflater)
            {
                menuInflater.inflate(R.menu.menu_image_grid, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull android.view.MenuItem menuItem)
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
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (getActivity() == null)
        {
            Log.e(TAG, "Fragment is not attached to an activity");
            return;
        }

        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null)
        {
            photoUri = createImageUri();

            if (photoUri != null)
            {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                takePictureLauncher.launch(takePictureIntent);
            }
            else
            {
                Log.e(TAG, "Failed to create image URI");
            }
        }
        else
        {
            Log.w(TAG, "No camera app available to handle the intent");
        }
    }

    private android.net.Uri createImageUri()
    {
        if (getActivity() == null)
        {
            Log.e(TAG, "Activity is null, cannot create URI");
            return null;
        }

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            Log.e(TAG, "External storage is not mounted");
            return null;
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/app_images");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            values.put(MediaStore.Images.Media.IS_PENDING, 1);
        }

        android.net.Uri uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if (uri != null)
        {
            Log.d(TAG, "Image URI created: " + uri);
        }
        else
        {
            Log.e(TAG, "Failed to create image URI");
        }

        return uri;
    }

    private void refreshImageList()
    {
        imageUris.clear();

        String permissionToCheck = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (getContext() == null || ContextCompat.checkSelfPermission(getContext(), permissionToCheck) != PackageManager.PERMISSION_GRANTED)
        {
            Log.w(TAG, "Missing permission to read images: " + permissionToCheck);
            return;
        }

        if (getActivity() == null)
        {
            Log.e(TAG, "Fragment is not attached to an activity, cannot query MediaStore");
            return;
        }

        String[] projection = {MediaStore.Images.Media._ID};
        Cursor cursor = getActivity().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                MediaStore.Images.Media.RELATIVE_PATH + " LIKE ?",
                new String[]{"%app_images%"},
                MediaStore.Images.Media.DATE_ADDED + " DESC"
        );

        if (cursor != null)
        {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

            while (cursor.moveToNext())
            {
                long id = cursor.getLong(idColumn);
                android.net.Uri uri = android.net.Uri.withAppendedPath(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));
                imageUris.add(uri);
            }
            cursor.close();
        }
        else
        {
            Log.w(TAG, "Failed to query MediaStore for images");
        }
        if (adapter != null)
        {
            adapter.notifyDataSetChanged();
        }
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

            android.net.Uri imageUri = imageUris.get(position);
            Glide.with(ImageGridFragment.this)
                    .load(imageUri)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .thumbnail(Glide.with(ImageGridFragment.this)
                            .load(imageUri)
                            .override(200, 200))
                    .error(android.R.drawable.ic_menu_close_clear_cancel)
                    .listener(new RequestListener<android.graphics.drawable.Drawable>()
                    {
                        @Override
                        public boolean onLoadFailed(@Nullable com.bumptech.glide.load.engine.GlideException e, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource)
                        {
                            Log.e(TAG, "Failed to load image URI: " + imageUri, e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource)
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