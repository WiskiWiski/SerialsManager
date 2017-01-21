package by.wiskiw.serialsmanager;

import android.content.Context;
import android.util.Log;

import by.wiskiw.serialsmanager.defaults.Constants;
import by.wiskiw.serialsmanager.objects.Serial;
import by.wiskiw.serialsmanager.storage.FirebaseDatabase;

/**
 * Created by WiskiW on 12.12.2016.
 */

public class SerialsActions {

    public static void onCreate(Context context, Serial serial) {
        // Serial created
        Log.d(Constants.TAG, "onSerialCreate");
        Notificator.checkNotificationData(context, serial);
    }

    public static void onRename(Context context, Serial oldSerial, Serial newSerial) {
        // Serial renamed
        Log.d(Constants.TAG, "onSerialRename");
        Notificator.cancelAlarm(context, oldSerial);
        newSerial.resetNotificationData();
        Notificator.checkNotificationData(context, newSerial);
        FirebaseDatabase.renameSerial(context, oldSerial, newSerial);
    }

    public static void onEpisodeUpdate(Context context, Serial serial) {
        // Serial season/episode update
        Log.d(Constants.TAG, "onSerialUpdate");
        Notificator.checkNotificationData(context, serial);
    }

    public static void onEdit(Context context, Serial serial) {
        // Serial edited/created
        Log.d(Constants.TAG, "onSerialEdit");
        FirebaseDatabase.saveSerial(context, serial);
    }

    public static void onDelete(Context context, Serial serial) {
        // Serial deleted
        Log.d(Constants.TAG, "onSerialDelete");
        FirebaseDatabase.deleteSerial(context, serial);
        Notificator.cancelAlarm(context, serial);
    }


}
