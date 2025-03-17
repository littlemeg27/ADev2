package com.example.contactdata;

import android.content.ContentResolver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// Brenna Pavlinchak
// AD2 - C202503
// ContactDetailsFragment

public class ContactDetailsFragment extends Fragment
{
    private TextView fullNameTextView;
    private LinearLayout phoneNumbersLayout;
    private ImageView photoImageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_contact_details, container, false);
        fullNameTextView = view.findViewById(R.id.full_name_text_view);
        phoneNumbersLayout = view.findViewById(R.id.phone_numbers_layout);
        photoImageView = view.findViewById(R.id.photo_image_view);
        return view;
    }

    public void updateDetails(long contactId)
    {
        ContentResolver contentResolver = requireContext().getContentResolver();
        ContactHelper.ContactDetails details = ContactHelper.getContactDetails(contentResolver, contactId);

        fullNameTextView.setText(details.fullName != null ? details.fullName : "Unknown");
        phoneNumbersLayout.removeAllViews();

        if (details.phoneNumbers != null)
        {
            for (String phoneNumber : details.phoneNumbers)
            {
                TextView phoneTextView = new TextView(getContext());
                phoneTextView.setText(phoneNumber);
                phoneNumbersLayout.addView(phoneTextView);
            }
        }
        if (details.photoUri != null)
        {
            photoImageView.setImageURI(details.photoUri);
        }
    }
}