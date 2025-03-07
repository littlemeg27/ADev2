package com.example.offlineapistorage;

import java.io.Serializable;

public class Post implements Serializable {
    private String title;
    private String author;
    private int score;

    public Post(String title, String author, int score) {
        this.title = title;
        this.author = author;
        this.score = score;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getScore() { return score; }

    @Override
    public String toString() {
        return score + " - " + title + " by " + author;
    }
}
