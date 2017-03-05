package by.wiskiw.serialsmanager.settings.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import by.wiskiw.serialsmanager.BuildConfig;
import by.wiskiw.serialsmanager.R;
import by.wiskiw.serialsmanager.defaults.Constants;

/**
 * Created by WiskiW on 26.12.2016.
 */

public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String TAG = Constants.TAG + ":SettingsFrg";

    private boolean showed1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showed1 = false;
        addPreferencesFromResource(R.xml.preferences_main);

        setVersionButton();

    }

    private void setVersionButton() {
        String versionKey = getString(R.string.pref_screen_key_version);
        Preference versionPreference = findPreference(versionKey);
        final String vName =  BuildConfig.VERSION_NAME;
        final int vCode = BuildConfig.VERSION_CODE;

        String apkType;
        if (BuildConfig.DEBUG) {
            apkType = "Debug";
        } else {
            apkType = "Release";
        }

        versionPreference.setSummary(apkType + " " + vName);
        versionPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Context context = getActivity().getApplicationContext();
                Toast.makeText(context, "VERSION_NAME: " + vName + "\n"
                        + " VERSION_CODE: " + vCode, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String sortingOrderKey = getString(R.string.pref_screen_key_sorting_order);
        if (key.equals(sortingOrderKey)) {

            if (!showed1) {
                Toast.makeText(getActivity().getApplicationContext(),
                        getString(R.string.toast_apply_after_restart), Toast.LENGTH_LONG).show();
                showed1 = true;
            }
        }
    }



}
