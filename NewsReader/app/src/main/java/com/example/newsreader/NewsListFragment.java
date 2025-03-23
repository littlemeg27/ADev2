package com.example.newsreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
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
import java.util.List;

public class NewsListFragment extends ListFragment {
    private static final String TAG = "NewsListFragment";
    private static final String PREFS_NAME = "NewsPrefs";
    private static final String KEY_ARTICLES = "saved_articles";
    private List<NewsArticle> articles;
    private ArrayAdapter<NewsArticle> adapter;
    private BroadcastReceiver uiRefreshReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load saved articles
        articles = loadArticles();
        adapter = new ArrayAdapter<NewsArticle>(requireContext(), R.layout.list_item_news, R.id.title, articles) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                NewsArticle article = getItem(position);
                if (article != null) {
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Register UI refresh receiver
        uiRefreshReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Received UI refresh broadcast");
                articles.clear();
                articles.addAll(loadArticles());
                adapter.notifyDataSetChanged();
            }
        };
        IntentFilter filter = new IntentFilter("com.example.newsapp.ACTION_REFRESH_UI");
        requireContext().registerReceiver(uiRefreshReceiver, filter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requireContext().unregisterReceiver(uiRefreshReceiver);
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        NewsArticle article = articles.get(position);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl()));
        startActivity(intent);
    }

    private List<NewsArticle> loadArticles() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
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
}
