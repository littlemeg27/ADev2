package com.example.webforms.Fragments;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.webforms.Operations.DatabaseHelper;
import com.example.webforms.Operations.DetailsActivity;
import com.example.webforms.Operations.Item;
import com.example.webforms.R;

import java.util.List;

// Brenna Pavlinchak
// AD2 - C202503
// ListFragment

public class ListFragment extends Fragment
{

    private static final String TAG = "ListFragment";
    private DatabaseHelper dbHelper;
    private WebView webView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        webView = view.findViewById(R.id.webview);

        webView.getSettings().setJavaScriptEnabled(false);

        webView.setWebViewClient(new WebViewClient()
        {
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                return handleUrl(url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request)
            {
                return handleUrl(request.getUrl().toString());
            }

            private boolean handleUrl(String url) {
                Log.d(TAG, "shouldOverrideUrlLoading: " + url);

                if (url.startsWith("my-app-schema://details-screen/"))
                {
                    String idStr = url.replace("my-app-schema://details-screen/", "");
                    try
                    {
                        long id = Long.parseLong(idStr);
                        Intent intent = new Intent(requireContext(), DetailsActivity.class);
                        intent.putExtra("ITEM_ID", id);
                        startActivity(intent);
                        return true;
                    }
                    catch (NumberFormatException e)
                    {
                        Log.e(TAG, "Invalid item ID in URL: " + url, e);
                    }
                }
                return false;
            }
        });

        loadItems();
        return view;
    }

    public void loadItems()
    {
        Log.d(TAG, "Loading items into WebView");
        List<Item> items;

        try (SQLiteDatabase db = dbHelper.getReadableDatabase())
        {
            items = dbHelper.getAllItems(db);
        }
        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        html.append("<h1>Items</h1>");

        if (items.isEmpty())
        {
            html.append("<p>Please add an item to get started.</p>");
        }
        else
        {
            html.append("<ul>");
            for (Item item : items)
            {
                html.append("<li><a href=\"my-app-schema://details-screen/").append(item.getId()).append("\">")
                        .append(item.getTitle()).append("</a></li>");
            }
            html.append("</ul>");
        }
        html.append("</body></html>");
        webView.loadDataWithBaseURL(null, html.toString(), "text/html", "UTF-8", null);
        Log.d(TAG, "Loaded HTML: " + html.toString());
    }
}