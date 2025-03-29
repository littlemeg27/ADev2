package com.example.webforms.Operations;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

// Brenna Pavlinchak
// AD2 - C202503
// DatabaseHelper

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "ItemsDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "items";
    private static final String COL_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_QUANTITY = "quantity";

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TITLE + " TEXT, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_QUANTITY + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertItem(SQLiteDatabase db, String title, String description, int quantity)
    {
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, title);
        values.put(COL_DESCRIPTION, description);
        values.put(COL_QUANTITY, quantity);
        long id = db.insert(TABLE_NAME, null, values);
        android.util.Log.d("DatabaseHelper", "Inserted item with ID: " + id);
        return id;
    }

    public List<Item> getAllItems(SQLiteDatabase db)
    {
        List<Item> items = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst())
        {
            do
            {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_QUANTITY));
                items.add(new Item(id, title, description, quantity));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    public Item getItemById(SQLiteDatabase db, long id)
    {
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_ID + " = ?", new String[]{String.valueOf(id)});
        Item item = null;

        if (cursor.moveToFirst())
        {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_QUANTITY));
            item = new Item(id, title, description, quantity);
        }
        cursor.close();
        return item;
    }

    public void deleteItem(SQLiteDatabase db, long id)
    {
        db.delete(TABLE_NAME, COL_ID + " = ?", new String[]{String.valueOf(id)});
    }
}