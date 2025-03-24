package com.example.newsreader;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.annotation.SuppressLint;

// Brenna Pavlinchak
// AD2 - C202503
// NewsListFragment

public class NewsListFragment extends ListFragment
{
    private UIRefreshReceiver uiRefreshReceiver;
    private static final String TAG = "NewsListFragment";
    private static final String PREFS_NAME = "NewsPrefs";
    private static final String KEY_ARTICLES = "saved_articles";
    private List<NewsArticle> articles;
    private ArrayAdapter<NewsArticle> adapter;
    private static final int RECEIVER_NOT_EXPORTED = 2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        articles = loadArticles();
        adapter = new ArrayAdapter<>(requireContext(), R.layout.list_item_news, R.id.title, articles)
        {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull android.view.ViewGroup parent)
            {
                View view = super.getView(position, convertView, parent);
                NewsArticle article = getItem(position);

                if (article != null)
                {
                    android.widget.TextView title = view.findViewById(R.id.title);
                    android.widget.TextView url = view.findViewById(R.id.url);
                    title.setText(article.getTitle());
                    url.setText(article.getUrl());
                }
                return view;
            }
        };
        setListAdapter(adapter);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(TAG, "Registering UI refresh receiver");
        registerUIReceiver();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.d(TAG, "Unregistering UI refresh receiver");
        unregisterUIReceiver();
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void registerUIReceiver()
    {
        uiRefreshReceiver = new UIRefreshReceiver(this);
        IntentFilter filter = new IntentFilter("com.example.newsreader.ACTION_REFRESH_UI");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            requireContext().registerReceiver(uiRefreshReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        }
        else
        {
            requireContext().registerReceiver(uiRefreshReceiver, filter);
        }
    }

    private void unregisterUIReceiver()
    {
        if (uiRefreshReceiver != null)
        {
            requireContext().unregisterReceiver(uiRefreshReceiver);
        }
    }

    public void refreshArticles()
    {
        articles.clear();
        articles.addAll(loadArticles());
        adapter.notifyDataSetChanged();
    }

    private List<NewsArticle> loadArticles()
    {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_ARTICLES, null);

        if (json == null)
        {
            return new ArrayList<>();
        }

        Gson gson = new Gson();
        NewsArticle[] articlesArray = gson.fromJson(json, NewsArticle[].class);
        List<NewsArticle> articles = new ArrayList<>();
        Collections.addAll(articles, articlesArray);
        return articles;
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id)
    {
        NewsArticle article = articles.get(position);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl()));
        startActivity(intent);
    }
}