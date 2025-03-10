package com.example.offlineapistorage;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

// Brenna Pavlinchak
// AD2 - C202503
// MainActivity

public class MainActivity extends AppCompatActivity
{
    private static final String[] TOKENS = {"AndroidDev", "gaming", "technology"};
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = findViewById(R.id.spinner);
        listView = findViewById(R.id.listView);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, TOKENS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedToken = parent.getItemAtPosition(position).toString();
                handleSelection(selectedToken);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void handleSelection(String token)
    {
        listView.setAdapter(null);

        if (isOnline())
        {
            Log.d("HandleSelection", "Device is online, downloading data for " + token);
            List<Post> posts = fetchData(token);

            if (posts != null && !posts.isEmpty())
            {
                Log.d("HandleSelection", "Data downloaded successfully, saving to file");
                saveToFile(token, posts);
                displayData(posts);
                Toast.makeText(this, "Fresh data downloaded", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Log.e("HandleSelection", "Failed to download data");
                Toast.makeText(this, "Error downloading data", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Log.d("HandleSelection", "Device is offline, attempting to load data for " + token);
            List<Post> posts = loadFromFile(token);

            if (posts != null && !posts.isEmpty())
            {
                Log.d("HandleSelection", "Offline data loaded successfully");
                displayData(posts);
                Toast.makeText(this, "Displaying offline data", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Log.d("HandleSelection", "No offline data available");
                Toast.makeText(this, "No data available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private List<Post> fetchData(String token)
    {
        String urlString = "https://www.reddit.com/r/" + "movies" + "/hot.json";
        try
        {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream inputStream = conn.getInputStream();
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            String jsonString = new String(bytes, StandardCharsets.UTF_8);
            Log.d("FetchData", "Downloaded JSON: " + jsonString.substring(0, Math.min(jsonString.length(), 100)));
            return parseJson(jsonString);
        }
        catch (Exception e)
        {
            Log.e("FetchData", "Error downloading data", e);
            return null;
        }
    }

    private List<Post> parseJson(String jsonString)
    {
        List<Post> posts = new ArrayList<>();

        try
        {
            JSONObject json = new JSONObject(jsonString);
            JSONArray children = json.getJSONObject("data").getJSONArray("children");

            for (int i = 0; i < children.length(); i++)
            {
                JSONObject postData = children.getJSONObject(i).getJSONObject("data");
                String title = postData.getString("title");
                String author = postData.getString("author");
                int score = postData.getInt("score");
                posts.add(new Post(title, author, score));
            }

            Log.d("ParseJson", "Parsed " + posts.size() + " posts");
        }
        catch (Exception e)
        {
            Log.e("ParseJson", "Error parsing JSON", e);
        }
        return posts;
    }

    private void saveToFile(String token, List<Post> posts)
    {
        try
        {
            FileOutputStream fos = openFileOutput(token + ".dat", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(posts);
            oos.close();
            fos.close();
            Log.d("SaveToFile", "Data saved for " + token);
        }
        catch (Exception e)
        {
            Log.e("SaveToFile", "Error saving data", e);
            Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show();
        }
    }

    private List<Post> loadFromFile(String token)
    {
        try
        {
            FileInputStream fis = openFileInput(token + ".dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            @SuppressWarnings("unchecked")
            List<Post> posts = (List<Post>) ois.readObject();
            ois.close();
            fis.close();
            Log.d("LoadFromFile", "Data loaded for " + token + ", size: " + posts.size());
            return posts;
        }
        catch (Exception e)
        {
            Log.e("LoadFromFile", "Error loading data", e);
            return null;
        }
    }

    private void displayData(List<Post> posts)
    {
        ArrayAdapter<Post> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, posts);
        listView.setAdapter(adapter);
        Log.d("DisplayData", "Displayed " + posts.size() + " posts");
    }
}