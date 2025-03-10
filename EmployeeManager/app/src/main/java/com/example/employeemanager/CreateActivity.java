package com.example.employeemanager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// Brenna Pavlinchak
// AD2 - C202503
// Create Activity

public class CreateActivity extends AppCompatActivity
{
    private DatabaseHelper db;
    private EditText etFirstName, etLastName, etEmpNumber, etHireDate, etStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        db = new DatabaseHelper(this);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmpNumber = findViewById(R.id.etEmployeeNumber);
        etHireDate = findViewById(R.id.etHireDate);
        etStatus = findViewById(R.id.etEmploymentStatus);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_save)
        {
            saveEmployee();
            return true;
        }
        else if (id == android.R.id.home)
        {
            finish(); // Handle back arrow press
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveEmployee()
    {
        try
        {
            Employee employee = new Employee(
                    etFirstName.getText().toString().trim(),
                    etLastName.getText().toString().trim(),
                    Integer.parseInt(etEmpNumber.getText().toString().trim()),
                    etHireDate.getText().toString().trim(),
                    etStatus.getText().toString().trim()
            );

            long id = db.createEmployee(employee);
            if (id != -1)
            {
                Toast.makeText(this, "Employee created successfully", Toast.LENGTH_SHORT).show();
                finish(); // Return to MainActivity
            }
            else
            {
                Toast.makeText(this, "Error creating employee", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show();
        }
    }
}