package com.example.downloadworker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.Map;

// Brenna Pavlinchak
// AD2 - C202503
// MainActivity

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions ->
                {
                    boolean allGranted = permissions.entrySet().stream()
                            .allMatch(Map.Entry::getValue);
                    if (allGranted)
                    {
                        loadFragments();
                    }
                    else
                    {
                        Toast.makeText(this, "Permissions denied. Cannot download images.", Toast.LENGTH_LONG).show();
                    }
                });

        String[] permissions = new String[]
                {
                Manifest.permission.INTERNET
        };
        boolean allPermissionsGranted = true;

        for (String permission : permissions)
        {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
            {
                allPermissionsGranted = false;
                break;
            }
        }
        if (!allPermissionsGranted)
        {
            requestPermissionLauncher.launch(permissions);
        }
        else
        {
            loadFragments();
        }
    }

    private void loadFragments()
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new ImageGridFragment());
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_download_images)
        {
            startDownloadWorker();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startDownloadWorker()
    {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest downloadWorkRequest = new OneTimeWorkRequest.Builder(ImageDownloadWorker.class)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(this).enqueue(downloadWorkRequest);
        Toast.makeText(this, "Download started", Toast.LENGTH_SHORT).show();
    }
}