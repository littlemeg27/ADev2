package com.example.appa;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.text.SimpleDateFormat;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->
                {
                    if (result.getResultCode() == FragmentActivity.RESULT_OK)
                    {
                        Log.d(TAG, "Photo captured successfully at URI: " + photoUri);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && photoUri != null)
                        {
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.Images.Media.IS_PENDING, 0);
                            getActivity().getContentResolver().update(photoUri, values, null, null);
                        }

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
        view.findViewById(R.id.take_picture_button).setOnClickListener(v -> dispatchTakePictureIntent());
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

        android.net.Uri uri = getActivity().getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

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
}