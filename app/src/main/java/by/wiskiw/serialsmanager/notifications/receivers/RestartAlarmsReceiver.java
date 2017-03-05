package by.wiskiw.serialsmanager.notifications.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import by.wiskiw.serialsmanager.BootService;
import by.wiskiw.serialsmanager.defaults.Constants;


/**
 * Created by WiskiW on 15.12.2016.
 */

public class RestartAlarmsReceiver extends BroadcastReceiver {

    private static final String TAG = Constants.TAG + ":RestartAlarmsRec";

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
