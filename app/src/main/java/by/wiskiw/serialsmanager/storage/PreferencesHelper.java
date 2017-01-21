package by.wiskiw.serialsmanager.storage;

import android.content.Context;

import by.wiskiw.serialsmanager.R;
import by.wiskiw.serialsmanager.defaults.Constants;

/**
 * Created by WiskiW on 26.12.2016.
 */

public class PreferencesHelper {

    public static String getShortingMethod(Context context){
        String defaultValue = (context.getResources().getStringArray(R.array.sortingOrderValues))[0];
        String key = context.getString(R.string.pref_screen_key_sorting_order);
        return PreferencesStorage.getString(context, key, defaultValue);
    }

    public static boolean isNotificationsEnable(Context context) {
        String key = context.getString(R.string.pref_screen_key_new_episode_notification);
        return PreferencesStorage.getBoolean(context, key, Constants.DEFAULT_VALUE_SERIALS_NOTIFICATIONS);
    }

}
