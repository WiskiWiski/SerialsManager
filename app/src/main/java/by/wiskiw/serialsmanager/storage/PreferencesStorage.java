package by.wiskiw.serialsmanager.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONException;
import org.json.JSONObject;

import by.wiskiw.serialsmanager.BuildConfig;
import by.wiskiw.serialsmanager.app.Constants;
import by.wiskiw.serialsmanager.managers.AdManager;

/**
 * Created by WiskiW on 25.06.2016.
 */

public class PreferencesStorage {

    private static final String TAG = Constants.TAG + ":PrefsStorage";

    private static SharedPreferences sharedpreferences = null;
    private static JSONObject gJsonObject = null;

    public static boolean getBoolean(Context context, String TAG, boolean dDefault) {
        getSharedPreferences(context);
        return sharedpreferences.getBoolean(TAG, dDefault);
    }

    public static void saveBoolean(Context context, String TAG, boolean toSet) {
        getSharedPreferences(context);
        sharedpreferences.edit()
                .putBoolean(TAG, toSet)
                .apply();
    }

    public static String getString(Context context, String TAG, String dDefault) {
        getSharedPreferences(context);
        return sharedpreferences.getString(TAG, dDefault);
    }

    public static void saveString(Context context, String TAG, String toSet) {
        getSharedPreferences(context);
        sharedpreferences.edit()
                .putString(TAG, toSet)
                .apply();
    }

    public static int getInt(Context context, String TAG, int dDefault) {
        getSharedPreferences(context);
        return sharedpreferences.getInt(TAG, dDefault);
    }

    public static void saveInt(Context context, String TAG, int toSet) {
        getSharedPreferences(context);
        sharedpreferences.edit()
                .putInt(TAG, toSet)
                .apply();
    }


    public static void reset(Context context) {
        getSharedPreferences(context);
        sharedpreferences.edit()
                .clear()
                .apply();
    }

    public static JSONObject getJson(Context context) {
        if (gJsonObject == null) {
            String jsonString = getString(context, Constants.PREFERENCE_JSON, null);
            if (jsonString != null && !jsonString.isEmpty()) {
                try {
                    gJsonObject = new JSONObject(jsonString);
                } catch (JSONException e) {
                    e.printStackTrace();
                    gJsonObject = new JSONObject();
                }
            } else {
                gJsonObject = new JSONObject();
            }
        }
        return gJsonObject;
    }

    public static void saveJson(Context context, JSONObject jsonObject) {
        gJsonObject = jsonObject;
        getSharedPreferences(context);
        sharedpreferences.edit()
                .putString(Constants.PREFERENCE_JSON, jsonObject.toString())
                .apply();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        if (sharedpreferences == null) {
            sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return sharedpreferences;
    }

    public static void syncWithRemoteConfig(final Context context) {
        /*
            Получаем настройки от Firebase Remote Config
            и сохраняем в локальные Preferences
         */
        try {
            final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

            // TODO: Remove DEBUG mode in realise version
            // Create Remote Config Setting to enable developer mode.
            // Fetching configs from the server is normally limited to 5 requests per hour.
            // Enabling developer mode allows many more requests to be made per hour, so developers
            // can test different config values during development.
            // [START enable_dev_mode]
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setDeveloperModeEnabled(BuildConfig.DEBUG)
                    .build();
            firebaseRemoteConfig.setConfigSettings(configSettings);
            // [END enable_dev_mode]

            long cacheExpiration = 3600; // 1 hour
            // If in developer mode cacheExpiration is set to 0 so each fetch will retrieve values from
            // the server.
            if (firebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
                cacheExpiration = 0;
            }

            // [START fetch_config_with_callback]
            // cacheExpirationSeconds is set to cacheExpiration here, indicating that any previously
            // fetched and cached config would be considered expired because it would have been fetched
            // more than cacheExpiration seconds ago. Thus the next fetch would go to the server unless
            // throttling is in progress. The default expiration duration is 43200 (12 hours).
            firebaseRemoteConfig.fetch(cacheExpiration)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Firebase Remote Config synchronization complete (Fetch Succeeded).");
                                // Once the config is successfully fetched it must be activated before newly fetched
                                // values are returned.
                                firebaseRemoteConfig.activateFetched();


                                getSharedPreferences(context);
                                sharedpreferences.edit()
                                        .putBoolean(AdManager.PREFERENCE_ADS_ENABLE,
                                                firebaseRemoteConfig.getBoolean(AdManager.PREFERENCE_ADS_ENABLE))
                                        .putBoolean(Constants.PREFERENCE_SEND_SERIALS,
                                                firebaseRemoteConfig.getBoolean(Constants.PREFERENCE_SEND_SERIALS))
                                        .putInt(AdManager.PREFERENCE_SHOW_AD_EVERY_CLICK_NUMB,
                                                (int) firebaseRemoteConfig.getLong(AdManager.PREFERENCE_SHOW_AD_EVERY_CLICK_NUMB))
                                        .putInt(AdManager.PREFERENCE_AD_ROW_INDEX,
                                                (int) firebaseRemoteConfig.getLong(AdManager.PREFERENCE_AD_ROW_INDEX))
                                        .putInt(Constants.PREFERENCE_MIN_IDENTITY_LVL,
                                                (int) firebaseRemoteConfig.getLong(Constants.PREFERENCE_MIN_IDENTITY_LVL))
                                        .apply();

                            } else {
                                Log.d(TAG, "Firebase Remote Config synchronization failed (Fetch failed).");
                            }
                        }
                    });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
