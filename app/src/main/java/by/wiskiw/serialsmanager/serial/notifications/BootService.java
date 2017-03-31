package by.wiskiw.serialsmanager.serial.notifications;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import by.wiskiw.serialsmanager.app.Constants;
import by.wiskiw.serialsmanager.serial.Serial;
import by.wiskiw.serialsmanager.serial.notifications.data.NotificationDataRequest;
import by.wiskiw.serialsmanager.settings.SettingsHelper;
import by.wiskiw.serialsmanager.storage.json.JsonDatabase;


/**
 * Created by WiskiW on 15.12.2016.
 */

public class BootService extends IntentService implements NotificationDataRequest.OnDataRequestListener {

    private static final String TAG = Constants.TAG + ":BootService";

    private List<Serial> serials;
    private int index;

    public BootService() {
        super("BootService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Reset all alarms here
        Log.d(TAG, "BootService has been successfully started");
        Context context = getApplicationContext();
        if (SettingsHelper.isNotificationsEnable(context)) {
            serials = JsonDatabase.getSerials(context);
            requestNotificationData();
        }
    }

    private void requestNotificationData() {
        new NotificationDataRequest(getApplicationContext())
                .setDataRequestListener(this)
                .setSerial(serials.get(index))
                .requestAlarm();

    }

    @Override
    public void onSuccess(Serial serial) {
        Context context = getApplicationContext();
        JsonDatabase.saveSerial(context, serial);

        index++;
        if (index < serials.size()) {
            requestNotificationData();
        }
    }

    @Override
    public void onFailed(int errCode, String msg) {

    }
}
