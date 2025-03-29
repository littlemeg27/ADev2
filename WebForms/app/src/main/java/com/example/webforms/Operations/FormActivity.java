package com.example.webforms.Operations;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.webforms.Fragments.WebViewFragment;
import com.example.webforms.R;

// Brenna Pavlinchak
// AD2 - C202503
// FormActivity

public class FormActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String html = "<html><body>" +
                "<h1>Add Item</h1>" +
                "<form>" +
                "Title: <input type=\"text\" id=\"title\"><br>" +
                "Description: <input type=\"text\" id=\"description\"><br>" +
                "Quantity: <input type=\"number\" id=\"quantity\"><br>" +
                "<button type=\"button\" onclick=\"saveData()\">Save</button>" +
                "</form>" +
                "<script>" +
                "function saveData() {" +
                "  var title = document.getElementById('title').value;" +
                "  var description = document.getElementById('description').value;" +
                "  var quantity = document.getElementById('quantity').value;" +
                "  Android.saveFormData(title, description, quantity);" +
                "}" +
                "</script>" +
                "</body></html>";

        WebViewFragment fragment = WebViewFragment.newInstance(html, true);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item)
    {

        if (item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}