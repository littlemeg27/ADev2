package com.example.sharingdata;

import android.net.Uri;

// Brenna Pavlinchak
// AD2 - C202503
// HostContract

public class HostContract
{
    public static final String PERMISSION = "com.fullsail.android.host.read";
    public static final String AUTHORITY = "com.fullsail.android.host.provider";

    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_THUMBNAIL = "thumbnail";

    public static final String TABLE_NAME = "books";
    public static final String CONTENT_URI_BASE = "content://" + AUTHORITY + "/";

    public static final Uri CONTENT_URI_BOOKS = Uri.parse(CONTENT_URI_BASE + TABLE_NAME);
}