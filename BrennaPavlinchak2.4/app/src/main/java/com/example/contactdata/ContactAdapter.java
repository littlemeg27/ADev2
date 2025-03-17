package com.example.contactdata;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// Brenna Pavlinchak
// AD2 - C202503
// ContactAdapter

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>
{

    private List<ContactHelper.Contact> contacts;
    private OnContactClickListener clickListener;

    public interface OnContactClickListener
    {
        void onContactClick(long contactId);
    }

    public ContactAdapter(List<ContactHelper.Contact> contacts, OnContactClickListener clickListener)
    {
        this.contacts = contacts;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position)
    {
        ContactHelper.Contact contact = contacts.get(position);
        holder.nameTextView.setText(contact.displayName != null ? contact.displayName : "Unknown");
        holder.phoneTextView.setText(contact.phoneNumber != null ? contact.phoneNumber : "No phone");

        if (contact.photoUri != null)
        {
            holder.photoImageView.setImageURI(contact.photoUri);
        }
        holder.itemView.setOnClickListener(v -> clickListener.onContactClick(contact.id));
    }

    @Override
    public int getItemCount()
    {
        return contacts.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder
    {
        ImageView photoImageView;
        TextView nameTextView;
        TextView phoneTextView;

        public ContactViewHolder(@NonNull View itemView)
        {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photo_image_view);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            phoneTextView = itemView.findViewById(R.id.phone_text_view);
        }
    }
}
