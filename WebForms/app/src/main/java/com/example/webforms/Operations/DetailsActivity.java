package com.example.webforms.Operations;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.webforms.Fragments.WebViewFragment;
import com.example.webforms.R;

// Brenna Pavlinchak
// AD2 - C202503
// DetailsActivity

public class DetailsActivity extends AppCompatActivity
{
    private DatabaseHelper dbHelper;
    private long itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);
        itemId = getIntent().getLongExtra("ITEM_ID", -1);

        if (itemId == -1)
        {
            finish();
            return;
        }

        loadRecord();
    }

    private void loadRecord()
    {
        Item item;
        try (SQLiteDatabase db = dbHelper.getReadableDatabase())
        {
            item = dbHelper.getItemById(db, itemId);
        }

        String html = buildItemHtml(item);
        WebViewFragment fragment = WebViewFragment.newInstance(html, false);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private String buildItemHtml(Item item)
    {
        StringBuilder html = new StringBuilder();
        html.append("<html><body><h1>Item Details</h1>");

        if (item != null)
        {
            String title = item.getTitle();
            String description = item.getDescription();
            int quantity = item.getQuantity();

            html.append("<p>Title: ").append(title).append("</p>")
                    .append("<p>Description: ").append(description).append("</p>")
                    .append("<p>Quantity: ").append(quantity).append("</p>");
        }
        else
        {
            html.append("<p>Item not found.</p>");
        }
        html.append("</body></html>");
        return html.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_delete)
        {
            try (SQLiteDatabase db = dbHelper.getWritableDatabase())
            {
                dbHelper.deleteItem(db, itemId);
            }
            Intent broadcast = new Intent("UPDATE_UI");
            sendBroadcast(broadcast);
            finish();
            return true;
        }
        else if (id == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}