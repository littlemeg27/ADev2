package com.example.webforms.Operations;

// Brenna Pavlinchak
// AD2 - C202503
// Item

public class Item
{
    private long id;
    private String title;
    private String description;
    private int quantity;



    public Item(long id, String title, String description, int quantity)
    {
        this.id = id;
        this.title = title;
        this.description = description;
        this.quantity = quantity;
    }

    public long getId()
    {
        return id;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDescription()
    {
        return description;
    }

    public int getQuantity()
    {
        return quantity;
    }
}