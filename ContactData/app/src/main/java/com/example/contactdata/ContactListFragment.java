package com.example.contactdata;

import android.content.ContentResolver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

// Brenna Pavlinchak
// AD2 - C202503
// ContactListFragment

public class ContactListFragment extends Fragment
{
    private ContactAdapter adapter;
    private final List<ContactHelper.Contact> contacts = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ContactAdapter(contacts, contactId ->
        {
            ContactDetailsFragment detailsFragment = (ContactDetailsFragment) getParentFragmentManager().findFragmentById(R.id.details_fragment_container);

            if (detailsFragment != null)
            {
                detailsFragment.updateDetails(contactId);
            }
        });
        recyclerView.setAdapter(adapter);
        loadContacts();
        return view;
    }

    private void loadContacts()
    {
        ContentResolver contentResolver = requireContext().getContentResolver();
        long[] contactIds = ContactHelper.getAllIDs(contentResolver);

        int startPosition = contacts.size();

        for (long id : contactIds)
        {
            ContactHelper.Contact contact = ContactHelper.getContactSummary(contentResolver, id);
            contacts.add(contact);
        }

        int itemCount = contacts.size() - startPosition;
        if (itemCount > 0)
        {
            adapter.notifyItemRangeInserted(startPosition, itemCount);
        }
        else
        {
            adapter.notifyDataSetChanged();
        }
    }
}