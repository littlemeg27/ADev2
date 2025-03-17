package com.example.sharingdata;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Brenna Pavlinchak
// AD2 - C202503
// BookDatabaseHelper

public class BookDatabaseHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "Books.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_BOOKS = "books";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = HostContract.COLUMN_TITLE;
    public static final String COLUMN_DESCRIPTION = HostContract.COLUMN_DESCRIPTION;
    public static final String COLUMN_THUMBNAIL = HostContract.COLUMN_THUMBNAIL;

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_BOOKS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_THUMBNAIL + " TEXT)";

    public BookDatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        onCreate(db);
    }
}