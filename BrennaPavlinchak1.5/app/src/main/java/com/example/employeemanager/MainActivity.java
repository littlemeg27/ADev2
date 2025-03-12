package com.example.employeemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

// Brenna Pavlinchak
// AD2 - C202503
// MainActivity

public class MainActivity extends AppCompatActivity
{
    private ListView listView;
    private EmployeeAdapter adapter;
    private DatabaseHelper db;
    private Spinner sortColumnSpinner, sortDirectionSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        listView = findViewById(R.id.listView);
        sortColumnSpinner = findViewById(R.id.sortColumnSpinner);
        sortDirectionSpinner = findViewById(R.id.sortDirectionSpinner);

        ArrayAdapter<String> columnAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"First Name", "Hire Date"});
        columnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortColumnSpinner.setAdapter(columnAdapter);

        ArrayAdapter<String> directionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Ascending", "Descending"});
        directionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortDirectionSpinner.setAdapter(directionAdapter);

        sortColumnSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                loadEmployees();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // No action needed
            }
        });

        sortDirectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                loadEmployees();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // No action needed
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) ->
        {
            Employee employee = adapter.getItem(position);

            if (employee != null)
            {
                Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
                intent.putExtra("EMPLOYEE_ID", employee.getId());
                startActivity(intent);
            }
            else
            {
                Toast.makeText(MainActivity.this, "Employee not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            startActivity(new Intent(this, CreateActivity.class));
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEmployees();
    }

    private void loadEmployees() {
        String displayOrderBy = sortColumnSpinner.getSelectedItem().toString();
        String orderBy;

        switch (displayOrderBy)
        {
            case "First Name":
                orderBy = "first_name";
                break;
            case "Hire Date":
                orderBy = "hire_date";
                break;
            default:
                orderBy = "first_name";
        }

        String displayDirection = sortDirectionSpinner.getSelectedItem().toString();
        String direction;

        switch (displayDirection)
        {
            case "Ascending":
                direction = "ASC";
                break;
            case "Descending":
                direction = "DESC";
                break;
            default:
                direction = "ASC";
        }

        List<Employee> employees = db.getAllEmployees(orderBy, direction);
        adapter = new EmployeeAdapter(this, employees);
        listView.setAdapter(adapter);
    }
}