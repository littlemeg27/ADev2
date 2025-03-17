package com.example.contactdata;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

// Brenna Pavlinchak
// AD2 - C202503
// MainActivity

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->
        {
            if (isGranted)
            {
                loadFragments();
            }
            else
            {
                Toast.makeText(this, "Permission denied. Cannot access contacts.", Toast.LENGTH_LONG).show();
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
        }
        else
        {
            loadFragments();
        }

        findViewById(R.id.fab_add_contact).setOnClickListener(v ->
        {
            Intent addContactIntent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
            startActivity(addContactIntent);
        });
    }

    private void loadFragments()
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.list_fragment_container, new ContactListFragment());
        transaction.replace(R.id.details_fragment_container, new ContactDetailsFragment());
        transaction.commit();
    }
}