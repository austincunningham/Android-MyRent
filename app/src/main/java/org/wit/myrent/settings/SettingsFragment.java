package org.wit.myrent.settings;


import android.os.Bundle;
import android.preference.PreferenceFragment;
import org.wit.myrent.R;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private SharedPreferences prefs;
    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }
}