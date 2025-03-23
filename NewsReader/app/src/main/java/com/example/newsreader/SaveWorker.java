package com.example.newsreader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

public class SaveWorker extends Worker {
    private static final String TAG = "SaveWorker";
    private static final String PREFS_NAME = "NewsPrefs";
    private static final String KEY_ARTICLES = "saved_articles";

    public SaveWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            // Get the article data from input
            String title = getInputData().getString("article_title");
            String url = getInputData().getString("article_url");
            if (title == null || url == null) {
                Log.e(TAG, "Invalid article data");
                return Result.failure();
            }

            NewsArticle article = new NewsArticle(title, url);

            // Save the article to SharedPreferences
            saveArticle(article);

            // Send UI refresh broadcast
            sendUIRefreshBroadcast();
            Log.d(TAG, "Article saved and UI refresh broadcast sent: " + title);
            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Error in SaveWorker", e);
            return Result.failure();
        }
    }

    private void saveArticle(NewsArticle article) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        List<NewsArticle> articles = loadArticles();
        articles.add(article);

        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(articles);
        editor.putString(KEY_ARTICLES, json);
        editor.apply();
    }

    private List<NewsArticle> loadArticles() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_ARTICLES, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        NewsArticle[] articlesArray = gson.fromJson(json, NewsArticle[].class);
        List<NewsArticle> articles = new ArrayList<>();
        for (NewsArticle article : articlesArray) {
            articles.add(article);
        }
        return articles;
    }

    private void sendUIRefreshBroadcast() {
        Intent intent = new Intent("com.example.newsapp.ACTION_REFRESH_UI");
        getApplicationContext().sendBroadcast(intent);
    }
}
