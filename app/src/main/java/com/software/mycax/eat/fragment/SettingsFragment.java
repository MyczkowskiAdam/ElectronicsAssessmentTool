package com.software.mycax.eat.fragment;


import android.os.Bundle;
import android.preference.PreferenceFragment;

import androidx.fragment.app.Fragment;

import com.software.mycax.eat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment {


    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.menu_settings);
        addPreferencesFromResource(R.xml.pref_settings);
    }

}
