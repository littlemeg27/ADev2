package com.example.webforms.Operations;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.webforms.Fragments.ListFragment;
import com.example.webforms.R;

// Brenna Pavlinchak
// AD2 - C202503
// MainActivity

public class MainActivity extends AppCompatActivity
{
    private BroadcastReceiver uiUpdateReceiver;
    private ListFragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        uiUpdateReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if (listFragment != null)
                {
                    listFragment.loadItems();
                }
            }
        };
        ContextCompat.registerReceiver(
                this,
                uiUpdateReceiver,
                new IntentFilter("UPDATE_UI"),
                ContextCompat.RECEIVER_NOT_EXPORTED
        );

        loadFragment();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (listFragment != null)
        {
            listFragment.loadItems();
        }
    }

    private void loadFragment()
    {
        listFragment = new ListFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, listFragment);
        transaction.commit();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(uiUpdateReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item)
    {
        if (item.getItemId() == R.id.action_add)
        {
            Intent intent = new Intent(MainActivity.this, FormActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}