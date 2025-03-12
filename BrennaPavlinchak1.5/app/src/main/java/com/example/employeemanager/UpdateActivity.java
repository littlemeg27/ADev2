package com.example.employeemanager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

// Brenna Pavlinchak
// AD2 - C202503
// Update Activity

public class UpdateActivity extends AppCompatActivity
{
    private DatabaseHelper db;
    private EditText etFirstName, etLastName, etEmpNumber, etHireDate, etStatus;
    private Employee employee;
    private long employeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        db = new DatabaseHelper(this);

        employeeId = getIntent().getLongExtra("EMPLOYEE_ID", -1);
        employee = db.getEmployee(employeeId);

        if (employee == null)
        {
            Toast.makeText(this, "Employee not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmpNumber = findViewById(R.id.etEmployeeNumber);
        etHireDate = findViewById(R.id.etHireDate);
        etStatus = findViewById(R.id.etEmploymentStatus);

        populateFields();

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_update, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_update)
        {
            updateEmployee();
            return true;
        }
        else if (id == R.id.action_delete)
        {
            confirmDelete();
            return true;
        }
        else if (id == android.R.id.home)
        {
            finish(); // Handle back arrow press
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateFields()
    {
        etFirstName.setText(employee.getFirstName());
        etLastName.setText(employee.getLastName());
        etEmpNumber.setText(String.valueOf(employee.getEmployeeNumber()));
        etHireDate.setText(employee.getHireDate());
        etStatus.setText(employee.getEmploymentStatus());
    }

    private void updateEmployee()
    {
        try
        {
            employee.setFirstName(etFirstName.getText().toString().trim());
            employee.setLastName(etLastName.getText().toString().trim());
            employee.setEmployeeNumber(Integer.parseInt(etEmpNumber.getText().toString().trim()));
            employee.setHireDate(etHireDate.getText().toString().trim());
            employee.setEmploymentStatus(etStatus.getText().toString().trim());

            int rows = db.updateEmployee(employee);

            if (rows > 0)
            {
                Toast.makeText(this, "Employee updated", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Error updating employee", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete()
    {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this employee?")
                .setPositiveButton("Yes", (dialog, which) ->
                {
                    db.deleteEmployee(employeeId);
                    Toast.makeText(this, "Employee deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}