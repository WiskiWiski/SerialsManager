package by.wiskiw.serialsmanager.serial.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.util.TimeZone;

import by.wiskiw.serialsmanager.app.Constants;
import by.wiskiw.serialsmanager.app.Utils;
import by.wiskiw.serialsmanager.serial.Serial;
import by.wiskiw.serialsmanager.serial.notifications.data.NotificationDataProcessor;
import by.wiskiw.serialsmanager.serial.notifications.receivers.AlarmReceiver;

/**
 * Created by WiskiW on 05.03.2017.
 */

public class SeAlarmManager {

    private static final String TAG = Constants.TAG + ":SeAlarmManager";


    public static void setAlarm(Context context, Serial serial) {
        if (!NotificationDataProcessor.isAlarmAvailable(serial)) {
            // Данные не подходят для установки уведомления
            //Log.d(TAG, "Alarm didn't set: episode/season not ready to set alarm!");
            return;
        }

        long timeMs = serial.getNextEpisodeDateMs();
        if (timeMs == -1) {
            Log.d(TAG, "setAlarm: next episode date not found for " + serial.getName());
            return;
        }
        //Log.d(TAG, "UTC time:   " + calendar.getTimeInMillis());
        timeMs = timeMs + TimeZone.getDefault().getRawOffset(); // получаем местное время
        String timeStr = Utils.getDate(timeMs);
        Log.i(TAG, "Alarm set on(local) " + timeStr + " - " + timeMs);
        //Log.d(TAG, "Alarm set on(SCTms) " + timeStr + " - " + System.currentTimeMillis());
        //Log.d(TAG, "Alarm set on(local) " + timeStr + " - " + (timeMs + TimeZone.getDefault().getRawOffset()));
        //Log.d(TAG, "Local time: " + (calendar.getTimeInMillis() + TimeZone.getDefault().getRawOffset()));

        Toast.makeText(context, "Alarm set on " + timeStr, Toast.LENGTH_LONG).show();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("serial_name", serial.getName());
        intent.putExtra("episode", serial.getNextEpisode());
        intent.putExtra("season", serial.getNextSeason());
        intent.putExtra("notification_id", serial.getNotificationId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, serial.getNotificationId(), intent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMs, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeMs, pendingIntent);
        }
        // RTC_WAKEUP - будит из спятчки
        // RTC - если устроство с выключенным экраном, но не скит
    }

    public static void cancelAlarm(Context context, Serial serial) {
        int notificationId = serial.getNotificationId();
        if (notificationId == 0) {
            //Log.d(TAG, "CancelAlarm: no one alarm has been found for " + serial.getName());
            return;
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Notificator.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, 0);
        alarmManager.cancel(pendingIntent);
        //Log.d(TAG, "CancelAlarm: Cleaned up all alarms for " + serial.getName());
    }

}
