package com.example.employeemanager;

// Brenna Pavlinchak
// AD2 - C202503
// EmployeeAdapter

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EmployeeAdapter extends ArrayAdapter<Employee>
{
    private final Context context;
    private final List<Employee> employees;
    private final SharedPreferences prefs;

    public EmployeeAdapter(Context context, List<Employee> employees)
    {
        super(context, R.layout.list_item_employee, employees);
        this.context = context;
        this.employees = employees;
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        ViewHolder holder;

        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_employee, parent, false);
            holder = new ViewHolder();
            holder.nameTextView = convertView.findViewById(R.id.tvName);
            holder.empNumberTextView = convertView.findViewById(R.id.tvEmployeeNumber);
            holder.hireDateTextView = convertView.findViewById(R.id.tvHireDate);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        Employee employee = employees.get(position);
        String fullName = context.getString(R.string.full_name, employee.getFirstName(), employee.getLastName());
        holder.nameTextView.setText(fullName);
        holder.empNumberTextView.setText(String.valueOf(employee.getEmployeeNumber()));

        String dateFormat = prefs.getString("date_format", "format1");
        String formattedDate = formatDate(employee.getHireDate(), dateFormat);
        holder.hireDateTextView.setText(formattedDate);

        return convertView;
    }

    private String formatDate(String hireDate, String format)
    {
        try
        {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(hireDate);
            if (date == null)
            {
                return hireDate;
            }

            SimpleDateFormat outputFormat;
            if ("format1".equals(format))
            {
                outputFormat = new SimpleDateFormat("MMM d'st', yyyy", Locale.getDefault());
            }
            else if ("format2".equals(format))
            {
                outputFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
            }
            else if ("format3".equals(format))
            {
                outputFormat = new SimpleDateFormat("M/d/yyyy", Locale.getDefault());
            }
            else
            {
                outputFormat = new SimpleDateFormat("MMM d'st', yyyy", Locale.getDefault());
            }
            return outputFormat.format(date);
        }
        catch (Exception e)
        {
            return hireDate;
        }
    }

    private static class ViewHolder
    {
        TextView nameTextView;
        TextView empNumberTextView;
        TextView hireDateTextView;
    }
}