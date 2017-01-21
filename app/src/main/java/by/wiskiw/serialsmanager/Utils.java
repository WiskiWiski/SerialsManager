package by.wiskiw.serialsmanager;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import by.wiskiw.serialsmanager.defaults.Constants;
import by.wiskiw.serialsmanager.storage.PreferencesStorage;

/**
 * Created by WiskiW on 22.10.2016.
 */

public class Utils {

    public static boolean firstStart;

    public static boolean checkFirstStart(Context context) {
        String FIRST_START_TAG = context.getResources().getString(R.string.pref_tag_first_start);
        firstStart = PreferencesStorage.getBoolean(context, FIRST_START_TAG, true);
        if (firstStart) {
            PreferencesStorage.saveBoolean(context, FIRST_START_TAG, false);
            PreferencesStorage.saveInt(context,
                    Constants.PREFERENCE_OLD_FAG_LVL, BuildConfig.VERSION_CODE);
        }
        return firstStart;
    }

    public static void rateThisApp(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }

    }

    public static String getDate() {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(timeZone);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy", new Locale("ru"));
        dateFormat.setTimeZone(calendar.getTimeZone());
        return dateFormat.format(calendar.getTime());
    }

    public static boolean isFutureTime(long timeMs){
        long currentMs = Calendar.getInstance().getTimeInMillis() + TimeZone.getDefault().getRawOffset();
        //Log.d(Constants.TAG, "currentMs: " + currentMs);
        return timeMs > currentMs;
    }

    public static String getDate(long ms) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.ROOT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(calendar.getTime());
    }

    public static void fetchOldFag(Context context) {
        boolean boolOldFag = PreferencesStorage.getBoolean(context, "old_fag", false);
        int intOldFag = PreferencesStorage.getInt(context, Constants.PREFERENCE_OLD_FAG_LVL,
                Constants.DEFAULT_VALUE_OLD_FAG_LVL);
        if (boolOldFag && intOldFag == 999) {
            PreferencesStorage.saveInt(context, Constants.PREFERENCE_OLD_FAG_LVL, 3);
        } else {
            PreferencesStorage.saveInt(context,
                    Constants.PREFERENCE_OLD_FAG_LVL, BuildConfig.VERSION_CODE);
        }
    }
}
