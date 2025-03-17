package com.example.contactdata;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

// Brenna Pavlinchak
// AD2 - C202503
// ContactHelper

public class ContactHelper
{
    private static final String TAG = "ContactHelper";
    public static class Contact
    {
        public long id;
        public String displayName;
        public String phoneNumber;
        public Uri photoUri;

        public Contact(long id, String displayName, String phoneNumber, Uri photoUri)
        {
            this.id = id;
            this.displayName = displayName;
            this.phoneNumber = phoneNumber;
            this.photoUri = photoUri;
        }
    }

    public static class ContactDetails
    {
        public String fullName;
        public List<String> phoneNumbers;
        public Uri photoUri;

        public ContactDetails(String fullName, List<String> phoneNumbers, Uri photoUri)
        {
            this.fullName = fullName;
            this.phoneNumbers = phoneNumbers;
            this.photoUri = photoUri;
        }
    }

    public static long[] getAllIDs(ContentResolver contentResolver)
    {
        List<Long> ids = new ArrayList<>();
        Cursor cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                new String[]{ContactsContract.Contacts._ID},
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        );
        if (cursor != null)
        {
            while (cursor.moveToNext())
            {
                int columnIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);

                if (columnIndex >= 0)
                {
                    long id = cursor.getLong(columnIndex);
                    ids.add(id);
                    Log.d(TAG, "Found contact ID: " + id);
                }
                else
                {
                    Log.w(TAG, "Column " + ContactsContract.Contacts._ID + " not found in cursor");
                }
            }
            cursor.close();
        }
        Log.d(TAG, "Total contacts found: " + ids.size());
        return ids.stream().mapToLong(Long::longValue).toArray();
    }

    public static Contact getContactSummary(ContentResolver contentResolver, long contactId)
    {
        String displayName = null;
        String phoneNumber = null;
        Uri photoUri;

        Cursor nameCursor = contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME}, ContactsContract.Data.CONTACT_ID + " = ? AND " +
                        ContactsContract.Data.MIMETYPE + " = ?",
                new String[]
                        {
                        String.valueOf(contactId), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                },
                null
        );
        if (nameCursor != null && nameCursor.moveToFirst())
        {
            int columnIndex = nameCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME);

            if (columnIndex >= 0)
            {
                displayName = nameCursor.getString(columnIndex);
                Log.d(TAG, "Display name for contact " + contactId + ": " + displayName);
            }
            else
            {
                Log.w(TAG, "Column " + ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME + " not found in cursor");
            }
            nameCursor.close();
        }

        Cursor phoneCursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[]{String.valueOf(contactId)},
                null
        );

        if (phoneCursor != null && phoneCursor.moveToFirst())
        {
            int columnIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            if (columnIndex >= 0)
            {
                phoneNumber = phoneCursor.getString(columnIndex);
                Log.d(TAG, "Primary phone number for contact " + contactId + ": " + phoneNumber);
            }
            else
            {
                Log.w(TAG, "Column " + ContactsContract.CommonDataKinds.Phone.NUMBER + " not found in cursor");
            }
            phoneCursor.close();
        }

        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Log.d(TAG, "Photo URI for contact " + contactId + ": " + photoUri);

        return new Contact(contactId, displayName, phoneNumber, photoUri);
    }

    public static ContactDetails getContactDetails(ContentResolver contentResolver, long contactId) {
        String fullName = null;
        List<String> phoneNumbers = new ArrayList<>();
        Uri photoUri;

        Cursor nameCursor = contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME},
                ContactsContract.Data.CONTACT_ID + " = ? AND " +
                        ContactsContract.Data.MIMETYPE + " = ?",
                new String[]
                        {
                        String.valueOf(contactId), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                },
                null
        );
        if (nameCursor != null && nameCursor.moveToFirst())
        {
            int columnIndex = nameCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME);

            if (columnIndex >= 0)
            {
                fullName = nameCursor.getString(columnIndex);
                Log.d(TAG, "Full name for contact " + contactId + ": " + fullName);
            }
            else
            {
                Log.w(TAG, "Column " + ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME + " not found in cursor");
            }
            nameCursor.close();
        }

        Cursor phoneCursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[]{String.valueOf(contactId)},
                null
        );
        if (phoneCursor != null)
        {
            while (phoneCursor.moveToNext())
            {
                int columnIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                if (columnIndex >= 0)
                {
                    String number = phoneCursor.getString(columnIndex);
                    phoneNumbers.add(number);
                    Log.d(TAG, "Phone number for contact " + contactId + ": " + number);
                }
                else
                {
                    Log.w(TAG, "Column " + ContactsContract.CommonDataKinds.Phone.NUMBER + " not found in cursor");
                }
            }
            phoneCursor.close();
        }

        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Log.d(TAG, "Photo URI for contact " + contactId + ": " + photoUri);

        return new ContactDetails(fullName, phoneNumbers, photoUri);
    }
}