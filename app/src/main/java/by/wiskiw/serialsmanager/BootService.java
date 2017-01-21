package by.wiskiw.serialsmanager;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import by.wiskiw.serialsmanager.defaults.Constants;
import by.wiskiw.serialsmanager.objects.Serial;
import by.wiskiw.serialsmanager.storage.PreferencesHelper;
import by.wiskiw.serialsmanager.storage.json.JsonDatabase;


/**
 * Created by WiskiW on 15.12.2016.
 */

public class BootService extends IntentService {

    public BootService() {
        super("BootService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Reset all alarms here
        Log.d(Constants.TAG, "Successfully started BootService");
        Context context = getApplicationContext();
        if (PreferencesHelper.isNotificationsEnable(context)) {
            List<Serial> serials = JsonDatabase.getSerials(context);
            for (Serial serial : serials) {
                Notificator.checkNotificationData(context, serial);
            }
        }
    }
}
