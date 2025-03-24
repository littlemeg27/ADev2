package com.example.newsreader;

import java.io.Serializable;

public class NewsArticle implements Serializable
{
    private String title;
    private String url;

    public NewsArticle(String title, String url)
    {
        this.title = title;
        this.url = url;
    }

    public String getTitle()
    {
        return title;
    }

    public String getUrl()
    {
        return url;
    }
}