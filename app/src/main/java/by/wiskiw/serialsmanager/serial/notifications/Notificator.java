package by.wiskiw.serialsmanager.serial.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import by.wiskiw.serialsmanager.R;
import by.wiskiw.serialsmanager.app.Constants;
import by.wiskiw.serialsmanager.main.activities.MainActivity;
import by.wiskiw.serialsmanager.settings.SettingsHelper;

/**
 * Created by WiskiW on 07.12.2016.
 */

public class Notificator {

    private static final String TAG = Constants.TAG + ":Notificator";

    public static void showNotification(Context context, Intent receiverIntent) {
        if (!SettingsHelper.isNotificationsEnable(context)){
            Log.i(TAG, "Notification didn't showed: user disable notifications.");
            return;
        }

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification.Builder builder = new Notification.Builder(context);

        String serialName = receiverIntent.getStringExtra("serial_name");
        String title = serialName;
        String body = "Новая серия " + serialName;
        // TODO: Set Ticker
        String ticker = serialName;

        /*
        // TODO: Check season/episode exists
        receiverIntent.getIntExtra("season", 0);
        receiverIntent.getIntExtra("episode", 0);

        */

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle(title)
                .setTicker(ticker)
                .setContentText(body)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
        //.setDefaults(Notification.DEFAULT_ALL)
        ;

        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);


        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL;
        notification.ledARGB = ContextCompat.getColor(context, R.color.colorPrimaryDark);
        notification.ledOnMS = 500;
        notification.ledOffMS = 2000;

        int notificationId = receiverIntent.getIntExtra("notification_id", 0);
        notificationManager.notify(notificationId, notification);
    }
}
