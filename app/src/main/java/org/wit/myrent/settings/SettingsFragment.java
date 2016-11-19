package org.wit.myrent.settings;


import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import org.wit.myrent.R;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import static org.wit.android.helpers.IntentHelper.navigateUp;
import static org.wit.android.helpers.LogHelpers.info;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private SharedPreferences prefs;
    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        info(getActivity(),"Setting change - key : value = "+ key +":" +sharedPreferences.getString(key,""));
        // If the refresh frequency is changed send a broadcast so that alarm appropriately reset.
        String refreshIntervalKey = getActivity().getResources().getString(R.string.refresh_interval_preference_key);
        if(key.equals(refreshIntervalKey)) {
            getActivity().sendBroadcast(new Intent("org.wit.myrent.receivers.SEND_BROADCAST"));
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                navigateUp(getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}