package by.wiskiw.serialsmanager.storage.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import by.wiskiw.serialsmanager.defaults.Constants;


/**
 * Created by WiskiW on 16.10.2015.
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    static final String DATABASE_TABLE = "main";
    private static final int DATABASE_VERSION = 4;

    static final String SERIAL_COLUMN = "SERIAL";
    static final String SERIAL_NOTE_COLUMN = "SERIAL_NOTE";
    static final String SEASON_COLUMN = "SEASON";
    static final String EPISODE_COLUMN = "EPISODE";
    static final String EPISODE_PER_SEASON_COLUMN = "EPISODE_PER_SEASON";


    private static final String DATABASE_CREATE_SCRIPT = "CREATE TABLE " + DATABASE_TABLE
            + " ("
            + "_ID integer PRIMARY KEY AUTOINCREMENT, "
            + SERIAL_COLUMN + " TEXT NOT NULL, "
            + SERIAL_NOTE_COLUMN + " TEXT, "
            + SEASON_COLUMN + " TEXT, "
            + EPISODE_COLUMN + " TEXT, "
            + EPISODE_PER_SEASON_COLUMN + " TEXT"
            + ");";

    DataBaseHelper(Context context) {
        super(context, Constants.OLD_DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(Constants.TAG, "Creating database: " + Constants.OLD_DATABASE_NAME);
        db.execSQL(DATABASE_CREATE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}
