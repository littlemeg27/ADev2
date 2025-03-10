package com.example.employeemanager;

// Brenna Pavlinchak
// AD2 - C202503
// Employee

public class Employee
{
    private long id;
    private String firstName;
    private String lastName;
    private int employeeNumber;
    private String hireDate;
    private String employmentStatus;

    public Employee(String firstName, String lastName, int employeeNumber, String hireDate, String employmentStatus)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.employeeNumber = employeeNumber;
        this.hireDate = hireDate;
        this.employmentStatus = employmentStatus;
    }

    public Employee(long id, String firstName, String lastName, int employeeNumber, String hireDate, String employmentStatus)
    {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.employeeNumber = employeeNumber;
        this.hireDate = hireDate;
        this.employmentStatus = employmentStatus;
    }

    public Employee()
    {
        // Default constructor
    }

    public long getId()
    {
        return id;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public int getEmployeeNumber()
    {
        return employeeNumber;
    }

    public String getHireDate()
    {
        return hireDate;
    }

    public String getEmploymentStatus()
    {
        return employmentStatus;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public void setEmployeeNumber(int employeeNumber)
    {
        this.employeeNumber = employeeNumber;
    }

    public void setHireDate(String hireDate)
    {
        this.hireDate = hireDate;
    }

    public void setEmploymentStatus(String employmentStatus)
    {
        this.employmentStatus = employmentStatus;
    }

    @Override
    public String toString()
    {
        return "Employee" +
                "{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", employeeNumber=" + employeeNumber +
                ", hireDate='" + hireDate + '\'' +
                ", employmentStatus='" + employmentStatus + '\'' +
                '}';
    }
}