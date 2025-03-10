package com.example.employeemanager;

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
    private static final String DATABASE_NAME = "EmployeeDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_EMPLOYEES = "employees";

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_EMPLOYEES + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "first_name TEXT,"
                + "last_name TEXT,"
                + "employee_number INTEGER,"
                + "hire_date DATETIME,"
                + "employment_status TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLOYEES);
        onCreate(db);
    }

    public long createEmployee(Employee employee)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("first_name", employee.getFirstName());
        values.put("last_name", employee.getLastName());
        values.put("employee_number", employee.getEmployeeNumber());
        values.put("hire_date", employee.getHireDate());
        values.put("employment_status", employee.getEmploymentStatus());
        long id = db.insert(TABLE_EMPLOYEES, null, values);
        db.close();
        return id;
    }

    public List<Employee> getAllEmployees(String orderBy, String direction)
    {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_EMPLOYEES + " ORDER BY " + orderBy + " " + direction;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst())
        {
            do
            {
                Employee employee = new Employee(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getString(5)
                );
                employees.add(employee);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return employees;
    }

    public Employee getEmployee(long id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EMPLOYEES,
                new String[]{"id", "first_name", "last_name", "employee_number", "hire_date", "employment_status"},
                "id = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        Employee employee = null;

        if (cursor != null && cursor.moveToFirst())
        {
            employee = new Employee(
                    cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getString(4),
                    cursor.getString(5)
            );
            cursor.close();
        }
        db.close();
        return employee;
    }

    public int updateEmployee(Employee employee)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("first_name", employee.getFirstName());
        values.put("last_name", employee.getLastName());
        values.put("employee_number", employee.getEmployeeNumber());
        values.put("hire_date", employee.getHireDate());
        values.put("employment_status", employee.getEmploymentStatus());

        int rows = db.update(TABLE_EMPLOYEES, values, "id = ?",
                new String[]{String.valueOf(employee.getId())});
        db.close();
        return rows;
    }

    public void deleteEmployee(long id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EMPLOYEES, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteAllEmployees()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_EMPLOYEES);
        db.close();
    }
}