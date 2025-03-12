package com.example.offlineapistorage;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull; // Added import
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// Brenna Pavlinchak
// AD2 - C202503
// MainActivity

public class MainActivity extends AppCompatActivity
{
    private static final String[] TOKENS = {"Movies", "Mobile", "Technology"};
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        Spinner spinner = findViewById(R.id.spinner);

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
            Log.d("HandleSelection", "Device is online, starting download for " + token);
            new Thread(() ->
            {
                List<Post> posts = fetchData(token);
                runOnUiThread(() -> refreshUI(token, posts));
            }).start();
        }
        else
        {
            Log.d("HandleSelection", "Device is offline, attempting to load data for " + token);
            List<Post> posts = loadFromFile(token);
            refreshUI(token, posts);
        }
    }

    private void refreshUI(String token, List<Post> posts)
    {
        if (posts != null && !posts.isEmpty())
        {
            if (isOnline())
            {
                Log.d("RefreshUI", "Saving and displaying fresh data for " + token);
                saveToFile(token, posts);
                Toast.makeText(this, "Fresh data downloaded", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Log.d("RefreshUI", "Displaying offline data for " + token);
                Toast.makeText(this, "Displaying offline data", Toast.LENGTH_SHORT).show();
            }
            displayData(posts);
        }
        else
        {
            Log.e("RefreshUI", "No data available for " + token);
            Toast.makeText(this, isOnline() ? "Error downloading data" : "No data available", Toast.LENGTH_SHORT).show();
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
        String urlString = "https://www.reddit.com/r/" + token.toLowerCase() + "/hot.json";
        try
        {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "OfflineApiStorage/1.0 (by Mammoth_Caregiver_59)");

            try (InputStream inputStream = conn.getInputStream();
                 ByteArrayOutputStream baos = new ByteArrayOutputStream())
            {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1)
                {
                    baos.write(buffer, 0, bytesRead);
                }
                String jsonString = new String(baos.toByteArray(), StandardCharsets.UTF_8);
                Log.d("FetchData", "Downloaded JSON: " + jsonString.substring(0, Math.min(jsonString.length(), 100)));

                List<Post> posts = parseJson(jsonString);

                if (posts != null)
                {
                    Log.d("FetchData", "Total posts parsed: " + posts.size());
                    for (int i = 0; i < posts.size(); i++)
                    {
                        Log.d("FetchData", "Post " + (i + 1) + ": " + posts.get(i).toString());
                    }
                }
                else
                {
                    Log.e("FetchData", "No posts parsed");
                }
                return posts;
            }
            finally
            {
                conn.disconnect();
            }
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
            return posts;
        }
        catch (Exception e)
        {
            Log.e("ParseJson", "Error parsing JSON", e);
            return null;
        }
    }

    private void saveToFile(String token, List<Post> posts)
    {
        try
        {
            FileOutputStream fos = openFileOutput(token.toLowerCase() + ".dat", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(posts);
            oos.close();
            fos.close();
            Log.d("SaveToFile", "Data saved for " + token);
        }
        catch (Exception e)
        {
            Log.e("SaveToFile", "Error saving data", e);
            runOnUiThread(() -> Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show());
        }
    }

    private List<Post> loadFromFile(String token) {
        try
        {
            FileInputStream fis = openFileInput(token.toLowerCase() + ".dat");
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
        ArrayAdapter<Post> adapter = new ArrayAdapter<>(this, R.layout.list_item_post, posts)
        {
            @Override
            @NonNull
            public View getView(int position, View convertView, @NonNull ViewGroup parent)
            {
                if (convertView == null)
                {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_post, parent, false);
                }

                Post post = getItem(position);
                if (post != null)
                {
                    TextView tvTitle = convertView.findViewById(R.id.tvTitle);
                    TextView tvAuthor = convertView.findViewById(R.id.tvAuthor);
                    TextView tvScore = convertView.findViewById(R.id.tvScore);

                    tvTitle.setText(post.getTitle());
                    tvAuthor.setText(getString(R.string.author_format, post.getAuthor()));
                    tvScore.setText(getString(R.string.score_format, post.getScore()));
                }

                return convertView;
            }
        };
        listView.setAdapter(adapter);
        Log.d("DisplayData", "Displayed " + posts.size() + " posts");
    }
}