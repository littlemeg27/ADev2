package com.example.sharingdata;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

// Brenna Pavlinchak
// AD2 - C202503
// StatusFragment

public class StatusFragment extends Fragment
{
    private TextView statusText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        statusText = view.findViewById(R.id.statusText);
        return view;
    }

    public void updateStatus(String message)
    {
        if (statusText != null)
        {
            statusText.setText(message);
        }
    }
}