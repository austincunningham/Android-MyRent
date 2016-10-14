package org.wit.myrent.settings;


import android.os.Bundle;
import android.preference.PreferenceFragment;
import org.wit.myrent.R;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragment
{
    private SharedPreferences prefs;
    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        prefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
    }
}