package by.wiskiw.serialsmanager.app;

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

    public static String getDate() {
        // Return UTC
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy", new Locale("ru"));
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));  // чьё время получаем
        return dateFormat.format(calendar.getTime());
    }

    public static boolean isFutureTime(long timeMs){
        long currentMs = Calendar.getInstance().getTimeInMillis();
        return timeMs + TimeZone.getDefault().getRawOffset() > currentMs;
    }

    public static String getDate(long ms) {
        if (ms < 0) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.ROOT);
        //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // чьё время получаем
        return dateFormat.format(calendar.getTime());
    }

    public static boolean vibrate(Context c, int i) {
        Vibrator v = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(i);
        return false;
    }

}
