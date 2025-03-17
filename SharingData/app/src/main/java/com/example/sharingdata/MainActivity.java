package com.example.sharingdata;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Brenna Pavlinchak
// AD2 - C202503
// MainActivity

public class MainActivity extends AppCompatActivity
{
    private StatusFragment statusFragment;
    private BookDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        statusFragment = (StatusFragment) fragmentManager.findFragmentById(R.id.fragment_container);
        if (statusFragment == null)
        {
            statusFragment = new StatusFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, statusFragment)
                    .commit();
        }

        dbHelper = new BookDatabaseHelper(this);

        if (isDataPresent())
        {
            statusFragment.updateStatus("Data is present, open companion app to view data...");
        }
        else
        {
            startDownload();
        }
    }

    private boolean isDataPresent()
    {
        try (SQLiteDatabase db = dbHelper.getReadableDatabase())
        {
            try (Cursor cursor = db.query(HostContract.TABLE_NAME, null, null, null, null, null, null))
            {
                return cursor.getCount() > 0;
            }
        }
    }

    private void startDownload()
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new DownloadTask(this, statusFragment, dbHelper));
        executor.shutdown();
    }

    private static class DownloadTask implements Runnable
    {
        private final WeakReference<MainActivity> activityRef;
        private final StatusFragment statusFragment;
        private final BookDatabaseHelper dbHelper;

        DownloadTask(MainActivity activity, StatusFragment fragment, BookDatabaseHelper helper)
        {
            this.activityRef = new WeakReference<>(activity);
            this.statusFragment = fragment;
            this.dbHelper = helper;
        }

        @Override
        public void run()
        {
            MainActivity activity = activityRef.get();
            if (activity == null || activity.isFinishing() || activity.isDestroyed())
            {
                return;
            }

            activity.runOnUiThread(() -> statusFragment.updateStatus("Download in progress, please wait..."));

            String result = downloadData();
            if (result != null)
            {
                parseAndStoreData(result);
            }
            else
            {
                activity.runOnUiThread(() -> statusFragment.updateStatus("Network not connected, this app downloads remote data..."));
            }
        }

        private String downloadData()
        {
            try
            {
                URL url = new URL("https://www.googleapis.com/books/v1/volumes?q=Android+Development");
                URLConnection connection = url.openConnection();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream())))
                {
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null)
                    {
                        result.append(line);
                    }
                    return result.toString();
                }
            }
            catch (Exception e)
            {
                Log.e("COMPANION APP", "Download error: " + e.getMessage());
                return null;
            }
        }

        private void parseAndStoreData(String jsonString)
        {
            MainActivity activity = activityRef.get();

            if (activity == null || activity.isFinishing() || activity.isDestroyed())
            {
                return;
            }

            try
            {
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray items = jsonObject.getJSONArray("items");
                Log.i("COMPANION APP", "Parsing " + items.length() + " items");

                SQLiteDatabase db = dbHelper.getWritableDatabase();

                for (int i = 0; i < items.length(); i++)
                {
                    JSONObject item = items.getJSONObject(i);
                    JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                    String title = volumeInfo.optString("title", "");
                    String description = volumeInfo.optString("description", "");
                    String thumbnail = volumeInfo.optJSONObject("imageLinks") != null ?
                            volumeInfo.getJSONObject("imageLinks").optString("thumbnail", "") : "";

                    ContentValues values = new ContentValues();
                    values.put(HostContract.COLUMN_TITLE, title);
                    values.put(HostContract.COLUMN_DESCRIPTION, description);
                    values.put(HostContract.COLUMN_THUMBNAIL, thumbnail);
                    db.insert(HostContract.TABLE_NAME, null, values);

                    Log.i("COMPANION APP", "Added Row " + i + ": TITLE: " + title + " DESCRIPTION: " + description + " THUMBNAIL: " + thumbnail);
                }
                activity.runOnUiThread(() -> statusFragment.updateStatus("Data is present, open companion app to view data..."));
            }
            catch (Exception e)
            {
                Log.e("COMPANION APP", "Parse error: " + e.getMessage());
            }
        }
    }
}