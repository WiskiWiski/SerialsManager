package by.wiskiw.serialsmanager.storage.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import by.wiskiw.serialsmanager.R;
import by.wiskiw.serialsmanager.managers.AdManager;
import by.wiskiw.serialsmanager.objects.Serial;
import by.wiskiw.serialsmanager.defaults.Constants;
import by.wiskiw.serialsmanager.storage.PreferencesStorage;
import by.wiskiw.serialsmanager.storage.json.JsonDatabase;


/**
 * Created by WiskiW on 16.10.2015.
 */

public class SqlDatabaseManager {

    private static final String TAG = Constants.TAG + ":SqlDBManager";

    private static ArrayList<Serial> getSerials(SQLiteDatabase mSqLiteDatabase) {
        ArrayList<Serial> SERIALS = new ArrayList<>();
        Cursor c = mSqLiteDatabase.query(DatabaseHelper.DATABASE_TABLE, null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                String serialName = c.getString(c.getColumnIndex(DatabaseHelper.SERIAL_COLUMN));
                int episode = c.getInt(c.getColumnIndex(DatabaseHelper.EPISODE_COLUMN));
                int season = c.getInt(c.getColumnIndex(DatabaseHelper.SEASON_COLUMN));
                int episodePerSeason = c.getInt(c.getColumnIndex(DatabaseHelper.EPISODE_PER_SEASON_COLUMN));
                String note = c.getString(c.getColumnIndex(DatabaseHelper.SERIAL_NOTE_COLUMN));

                Serial serial = new Serial(serialName, episode, season, episodePerSeason, note);
                SERIALS.add(serial);
            } while (c.moveToNext());
        }
        closeCursor(c);
        return SERIALS;
    }

    private static void closeCursor(Cursor c) {
        if (c != null)
            c.close();
    }

    private static boolean isSqlDatabaseExist(Context context) {
        File database = context.getDatabasePath(Constants.OLD_DATABASE_NAME);
        //Log.d(Constants.TAG, "path = " + database.getPath());
        return database.exists();
    }

    public static boolean updateToJson(Context context) {
        if (isSqlDatabaseExist(context)) {
            SQLiteDatabase mSqLiteDatabase = new DatabaseHelper(context).getReadableDatabase();
            ArrayList<Serial> savedSerials = getSerials(mSqLiteDatabase);
            if (savedSerials != null && savedSerials.size() > 0) {
                int size = savedSerials.size();
                for (int i = 0; i < size; i++) {
                    JsonDatabase.saveSerial(context, savedSerials.get(i));
                }
                return true;
            }
            context.deleteDatabase(Constants.OLD_DATABASE_NAME);

            PreferencesStorage.saveInt(context, Constants.PREFERENCE_LAUNCH_COUNTER, 3);
            AdManager.disableAds(context);
        } else {
            Log.d(TAG, "not exist");
        }
        return false;
    }
}
