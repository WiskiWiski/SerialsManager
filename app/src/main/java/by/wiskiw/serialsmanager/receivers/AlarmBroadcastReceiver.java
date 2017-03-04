package by.wiskiw.serialsmanager.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import by.wiskiw.serialsmanager.Notificator;
import by.wiskiw.serialsmanager.defaults.Constants;
import by.wiskiw.serialsmanager.settings.SettingsHelper;

/**
 * Created by WiskiW on 15.12.2016.
 */

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SettingsHelper.isNotificationsEnable(context)) {
            Log.d(Constants.TAG, "Creating notification...");
            Notificator.createNotification(context, intent);
        }
    }
}
