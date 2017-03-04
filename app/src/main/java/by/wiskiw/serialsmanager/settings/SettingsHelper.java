package by.wiskiw.serialsmanager.settings;

import android.content.Context;

import by.wiskiw.serialsmanager.R;
import by.wiskiw.serialsmanager.defaults.Constants;
import by.wiskiw.serialsmanager.storage.PreferencesStorage;

/**
 * Created by WiskiW on 04.03.2017.
 */

public class SettingsHelper {

    public enum ShortingOrder {
        ALPHABET, DATE
    }

    public static ShortingOrder getShortingMethod(Context context) {
        String defaultValue = (context.getResources().getStringArray(R.array.sortingOrderValues))[0];
        String key = context.getString(R.string.pref_screen_key_sorting_order);
        String strOrder = PreferencesStorage.getString(context, key, defaultValue);
        switch (strOrder){
            case "alphabet_order":
                return ShortingOrder.ALPHABET;
            case "date_order":
                return ShortingOrder.DATE;
            default:
                return ShortingOrder.ALPHABET;
        }
    }

    public static boolean isNotificationsEnable(Context context) {
        String key = context.getString(R.string.pref_screen_key_new_episode_notification);
        return PreferencesStorage.getBoolean(context, key, Constants.DEFAULT_VALUE_SERIALS_NOTIFICATIONS);
    }


}
