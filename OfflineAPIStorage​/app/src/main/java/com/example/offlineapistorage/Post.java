package com.example.offlineapistorage;

import java.io.Serializable;
import androidx.annotation.NonNull;

// Brenna Pavlinchak
// AD2 - C202503
// Post

public class Post implements Serializable
{
    private final String title;
    private final String author;
    private final int score;

    public Post(String title, String author, int score)
    {
        this.title = title;
        this.author = author;
        this.score = score;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getScore() { return score; }

    @Override
    @NonNull
    public String toString()
    {
        String safeTitle = (title != null) ? title : "Unknown Title";
        String safeAuthor = (author != null) ? author : "Unknown Author";
        return score + " - " + safeTitle + " by " + safeAuthor;
    }
}