package com.example.newsreader;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DownloadWorker extends Worker {
    private static final String TAG = "DownloadWorker";
    private static final String API_URL = "https://www.reddit.com/r/NewsToday/hot.json";
    private static final String CHANNEL_ID = "news_channel";
    private static final int NOTIFICATION_ID = 1;

    public DownloadWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            // Fetch data from Reddit API
            String jsonResponse = downloadData();
            if (jsonResponse == null) {
                Log.e(TAG, "Failed to download data");
                return Result.failure();
            }

            // Parse JSON and select a random article
            NewsArticle article = parseRandomArticle(jsonResponse);
            if (article == null) {
                Log.e(TAG, "Failed to parse article");
                return Result.failure();
            }

            // Generate notification
            generateNotification(article);
            Log.d(TAG, "Notification generated for article: " + article.getTitle());
            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Error in DownloadWorker", e);
            return Result.failure();
        }
    }

    private String downloadData() throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            Log.e(TAG, "HTTP error code: " + responseCode);
            return null;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        connection.disconnect();
        return response.toString();
    }

    private NewsArticle parseRandomArticle(String jsonResponse) throws Exception {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONObject data = jsonObject.getJSONObject("data");
        JSONArray children = data.getJSONArray("children");

        List<NewsArticle> articles = new ArrayList<>();
        for (int i = 0; i < children.length(); i++) {
            JSONObject post = children.getJSONObject(i).getJSONObject("data");
            String title = post.optString("title");
            String url = post.optString("url");
            if (title != null && !title.isEmpty() && url != null && !url.isEmpty()) {
                articles.add(new NewsArticle(title, url));
            }
        }

        if (articles.isEmpty()) {
            return null;
        }

        Random random = new Random();
        return articles.get(random.nextInt(articles.size()));
    }

    private void generateNotification(NewsArticle article) {
        Context context = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel (required for API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Create PendingIntent for viewing the article (ACTION_VIEW)
        Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl()));
        PendingIntent viewPendingIntent = PendingIntent.getActivity(
                context,
                0,
                viewIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        // Create PendingIntent for saving the article (Broadcast)
        Intent saveIntent = new Intent("com.example.newsapp.ACTION_SAVE_ARTICLE");
        saveIntent.putExtra("article_title", article.getTitle());
        saveIntent.putExtra("article_url", article.getUrl());
        PendingIntent savePendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                saveIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_save)
                .setContentTitle("News Article")
                .setContentText(article.getTitle())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(article.getTitle()))
                .setContentIntent(viewPendingIntent)
                .addAction(R.drawable.ic_save, context.getString(R.string.action_save_article), savePendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
