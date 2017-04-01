package by.wiskiw.serialsmanager.settings.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import by.wiskiw.serialsmanager.BuildConfig;
import by.wiskiw.serialsmanager.R;
import by.wiskiw.serialsmanager.app.Constants;
import by.wiskiw.serialsmanager.settings.activities.SettingsActivity;
import by.wiskiw.serialsmanager.storage.external.ExternalStorageProvider;

/**
 * Created by WiskiW on 26.12.2016.
 */

public class SettingsFragment extends PreferenceFragment {

    private static final String TAG = Constants.TAG + ":SettingsFrg";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_main);

        setShortingMenu();
        setImportButton();
        setExportButton();
        setVersionButton();
    }

    private void setShortingMenu() {
        String shortingMenuKey = getString(R.string.pref_screen_key_sorting_order);
        ListPreference shortingMenuPreference = (ListPreference) findPreference(shortingMenuKey);

        shortingMenuPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                getActivity().setResult(SettingsActivity.RESULT_LIST_UPDATED, new Intent());
                return true;
            }
        });

    }

    private void setImportButton() {
        String versionKey = getString(R.string.pref_screen_key_import);
        Preference versionPreference = findPreference(versionKey);

        versionPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                ExternalStorageProvider.importDatabase(getActivity());
                return true;
            }
        });
    }

    private void setExportButton() {
        String exportBtnKey = getString(R.string.pref_screen_key_export);
        final EditTextPreference exportBtnPreference = (EditTextPreference) findPreference(exportBtnKey);

        final String defaultFilename = ExternalStorageProvider.generateFileName();
        exportBtnPreference.setDefaultValue(defaultFilename);
        exportBtnPreference.getEditText().setHint(defaultFilename);

        exportBtnPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String filename = newValue.toString().trim();
                if (filename.isEmpty()) {
                    filename = defaultFilename;
                } else {
                    int dotIndex = filename.lastIndexOf('.');
                    if (dotIndex > 0) {
                        filename = filename.substring(0, dotIndex);
                    }
                    filename = filename + ".json";
                }

                ExternalStorageProvider.exportDatabase(getActivity().getApplicationContext(), filename);
                return false;
            }
        });
    }

    private void setVersionButton() {
        String importBtnKey = getString(R.string.pref_screen_key_version);
        Preference versionPreference = findPreference(importBtnKey);
        final String vName = BuildConfig.VERSION_NAME;
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

}
