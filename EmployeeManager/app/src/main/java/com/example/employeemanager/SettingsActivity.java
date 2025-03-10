package com.example.employeemanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

// Brenna Pavlinchak
// AD2 - C202503
// Settings Activity

public class SettingsActivity extends AppCompatActivity
{
    private DatabaseHelper db;
    private Spinner dateFormatSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        db = new DatabaseHelper(this);

        dateFormatSpinner = findViewById(R.id.dateFormatSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Jan 1st, 2000", "01-01-2000", "1/1/2000"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateFormatSpinner.setAdapter(adapter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String savedFormat = prefs.getString("date_format", "format1");

        int position;

        if ("format2".equals(savedFormat))
        {
            position = 1;
        }
        else if ("format3".equals(savedFormat))
        {
            position = 2;
        }
        else
        {
            position = 0;
        }
        dateFormatSpinner.setSelection(position);

        dateFormatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                SharedPreferences.Editor editor = prefs.edit();
                String formatValue;
                if (position == 0)
                {
                    formatValue = "format1";
                }
                else if (position == 1)
                {
                    formatValue = "format2";
                }
                else if (position == 2)
                {
                    formatValue = "format3";
                }
                else
                {
                    formatValue = "format1";
                }
                editor.putString("date_format", formatValue);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // Do nothing
            }
        });

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_delete_all)
        {
            confirmDeleteAll();
            return true;
        }
        else if (id == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmDeleteAll()
    {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete All")
                .setMessage("Are you sure you want to delete all employees?")
                .setPositiveButton("Yes", (dialog, which) ->
                {
                    db.deleteAllEmployees();
                    Toast.makeText(this, "All employees deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }
}