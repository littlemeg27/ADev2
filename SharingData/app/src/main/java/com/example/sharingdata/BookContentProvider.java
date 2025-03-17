package com.example.sharingdata;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

// Brenna Pavlinchak
// AD2 - C202503
// BookContentProvider

public class BookContentProvider extends ContentProvider
{
    private static final int BOOKS = 1;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static
    {
        uriMatcher.addURI(HostContract.AUTHORITY, HostContract.TABLE_NAME, BOOKS);
    }

    private BookDatabaseHelper dbHelper;

    @Override
    public boolean onCreate()
    {
        dbHelper = new BookDatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder)
    {
        if (uriMatcher.match(uri) != BOOKS)
        {
            throw new IllegalArgumentException("Invalid URI: " + uri);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(HostContract.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        if (getContext() != null)
        {
            cursor.setNotificationUri(getContext().getContentResolver(), HostContract.CONTENT_URI_BOOKS);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri)
    {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values)
    {
        if (uriMatcher.match(uri) != BOOKS)
        {
            throw new IllegalArgumentException("Invalid URI: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert(HostContract.TABLE_NAME, null, values);

        if (getContext() != null)
        {
            getContext().getContentResolver().notifyChange(HostContract.CONTENT_URI_BOOKS, null);
        }
        return Uri.withAppendedPath(uri, String.valueOf(id));
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs)
    {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs)
    {
        return 0;
    }
}