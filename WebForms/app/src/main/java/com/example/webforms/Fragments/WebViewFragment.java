package com.example.webforms.Fragments;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.webforms.Operations.DatabaseHelper;
import com.example.webforms.Operations.DetailsActivity;
import com.example.webforms.R;

// Brenna Pavlinchak
// AD2 - C202503
// WebViewFragment

public class WebViewFragment extends Fragment
{
    private String htmlContent;
    private boolean enableJavaScript;
    private WebView webView;

    public static WebViewFragment newInstance(String htmlContent, boolean enableJavaScript)
    {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString("htmlContent", htmlContent);
        args.putBoolean("enableJavaScript", enableJavaScript);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            htmlContent = getArguments().getString("htmlContent");
            enableJavaScript = getArguments().getBoolean("enableJavaScript", false);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        webView = view.findViewById(R.id.webview);

        webView.getSettings().setJavaScriptEnabled(enableJavaScript);

        if (enableJavaScript)
        {
            webView.addJavascriptInterface(new WebAppInterface(), "Android");
        }

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

            private boolean handleUrl(String url)
            {
                if (url.startsWith("my-app-schema://details-screen/"))
                {
                    String[] parts = url.split("/");
                    long id = Long.parseLong(parts[parts.length - 1]);
                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    intent.putExtra("ITEM_ID", id);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        if (htmlContent != null)
        {
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
        }

        return view;
    }

    private class WebAppInterface
    {
        @JavascriptInterface
        public void saveFormData(String title, String description, String quantity)
        {
            Log.d("WebViewFragment", "Saving: " + title + ", " + description + ", " + quantity);
            DatabaseHelper dbHelper = new DatabaseHelper(WebViewFragment.this.getActivity());

            try (SQLiteDatabase db = dbHelper.getWritableDatabase())
            {
                long newItemId = dbHelper.insertItem(db, title, description, Integer.parseInt(quantity));
                Log.d("WebViewFragment", "New item inserted with ID: " + newItemId);
            }

            if (WebViewFragment.this.getActivity() != null)
            {
                Intent broadcast = new Intent("UPDATE_UI");
                WebViewFragment.this.getActivity().sendBroadcast(broadcast);
            }

            if (WebViewFragment.this.getActivity() != null)
            {
                WebViewFragment.this.getActivity().finish();
            }
        }
    }
}