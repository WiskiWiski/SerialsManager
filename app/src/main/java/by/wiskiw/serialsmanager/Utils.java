package by.wiskiw.serialsmanager;

import android.content.Context;
import android.os.Vibrator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import by.wiskiw.serialsmanager.defaults.Constants;

/**
 * Created by WiskiW on 22.10.2016.
 */

public class Utils {

    private static final String TAG = Constants.TAG + ":Utils";

    public static boolean firstStart;

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

    public static boolean vibrate(Context c, int i) {
        Vibrator v = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(i);
        return false;
    }

}
