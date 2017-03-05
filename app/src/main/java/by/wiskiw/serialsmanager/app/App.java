package by.wiskiw.serialsmanager.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import by.wiskiw.serialsmanager.BuildConfig;
import by.wiskiw.serialsmanager.defaults.Constants;
import by.wiskiw.serialsmanager.storage.PreferencesStorage;

/**
 * Created by WiskiW on 05.03.2017.
 */

public class App extends Application {

    private static final String TAG = Constants.TAG + ":App";

    private static boolean firstStart = false;
    private static int LAUNCH_COUNTER;

    @Override
    public void onCreate() {
        super.onCreate();
        checkFirstStart(getApplicationContext());
        initLaunch(getApplicationContext());
    }

    public static boolean isFirstStart() {
        return firstStart;
    }

    public static void firstStartComplete(Context context) {
        PreferencesStorage.saveBoolean(context,  Constants.PREFERENCE_FIRST_START, false);
    }

    private static void checkFirstStart(Context context) {
        firstStart = PreferencesStorage.getBoolean(context, Constants.PREFERENCE_FIRST_START, true);
        if (firstStart) {
            // First start staff
            int intOldFag = PreferencesStorage.getInt(context, "old_fag_lvl", -1);
            if (intOldFag == -1) {
                PreferencesStorage.saveInt(context, Constants.PREFERENCE_INSTALL_VERSION, BuildConfig.VERSION_CODE);
            } else {
                PreferencesStorage.saveInt(context, Constants.PREFERENCE_INSTALL_VERSION, intOldFag);

            }

            LAUNCH_COUNTER = 0;
            PreferencesStorage.saveInt(context, Constants.PREFERENCE_LAUNCH_COUNTER, LAUNCH_COUNTER);
            firstStartComplete(context);  // Регистрируем первый запуск успешным
        }
    }

    public static int getLaunchNumb() {
        return LAUNCH_COUNTER;
    }

    public static int getInstallVersion(Context context) {
        // -1 - Unknown or lower than 8
        return PreferencesStorage.getInt(context, Constants.PREFERENCE_INSTALL_VERSION, -1);
    }

    private static void initLaunch(Context context) {
        LAUNCH_COUNTER = PreferencesStorage.getInt(context,
                Constants.PREFERENCE_LAUNCH_COUNTER, 0);
        LAUNCH_COUNTER++;
        Log.d(TAG, "Initialising launch number " + LAUNCH_COUNTER);
        PreferencesStorage.saveInt(context, Constants.PREFERENCE_LAUNCH_COUNTER, LAUNCH_COUNTER);
    }
}
