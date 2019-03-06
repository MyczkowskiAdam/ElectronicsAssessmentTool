package com.software.mycax.eat.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import androidx.fragment.app.Fragment;

import com.software.mycax.eat.R;
import com.software.mycax.eat.Utils;
import com.software.mycax.eat.acitivities.UpdateProfileActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    public SettingsFragment() {
        // Required empty public constructor
    }

    //TODO: Finish Settings fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.menu_settings);
        addPreferencesFromResource(R.xml.pref_settings);
        Preference pUpdateProfile = findPreference(Utils.PREF_PROFILE_UPDATE);
        pUpdateProfile.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (Utils.PREF_PROFILE_UPDATE.equals(preference.getKey())) {
            startActivity(new Intent(getActivity(), UpdateProfileActivity.class));
        }
        return false;
    }
}
