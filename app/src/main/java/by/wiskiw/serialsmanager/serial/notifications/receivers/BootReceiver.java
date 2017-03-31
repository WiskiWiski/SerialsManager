package by.wiskiw.serialsmanager.serial.notifications.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import by.wiskiw.serialsmanager.app.Constants;
import by.wiskiw.serialsmanager.serial.notifications.BootService;

/**
 * Created by WiskiW on 05.03.2017.
 */

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = Constants.TAG + ":BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            // It is better to reset alarms using Background IntentService
            Intent i = new Intent(context, BootService.class);
            context.startService(i);
        } else {
            Log.e(TAG, "Received unexpected intent " + intent.toString());
        }
    }
}
