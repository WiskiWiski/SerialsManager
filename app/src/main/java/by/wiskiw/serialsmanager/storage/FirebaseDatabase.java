package by.wiskiw.serialsmanager.storage;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;

import java.util.Locale;

import by.wiskiw.serialsmanager.Utils;
import by.wiskiw.serialsmanager.defaults.Constants;
import by.wiskiw.serialsmanager.objects.Serial;


/**
 * Created by WiskiW on 03.12.2016.
 */

public class FirebaseDatabase {

    private static DatabaseReference firebaseDatabase;

    private static void initDatabaseReference() {
        if (firebaseDatabase == null) {
            //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            firebaseDatabase = com.google.firebase.database.FirebaseDatabase.getInstance().getReference();
            firebaseDatabase.keepSynced(true);
        }
    }

    private static boolean isSendingEnable(Context context) {
        return PreferencesStorage.getBoolean(context,
                Constants.PREFERENCE_SEND_SERIALS, Constants.DEFAULT_VALUE_SEND_SERIALS);
    }

    private static boolean isReadyToSend(Context context) {
        if (isSendingEnable(context)) {
            initDatabaseReference();
            if (firebaseDatabase != null) {
                return true;
            }
        }
        return false;
    }

    public static void onInteraction() {
        if (firebaseDatabase != null) {
            firebaseDatabase
                    .child("last_interaction")
                    .setValue(Utils.getDate());
        }
    }

    public static void deleteSerial(Context context, Serial serial) {
        if (isReadyToSend(context)) {
            String serialName = serial.getName().toLowerCase();
            firebaseDatabase
                    .child("serials_base")
                    .child(getLocale())
                    .child(serialName)
                    .removeValue();
            onInteraction();
        }
    }

    public static void saveSerial(Context context, Serial serial) {
        if (isReadyToSend(context)) {
            String serialName = serial.getName().toLowerCase();
            firebaseDatabase
                    .child("serials_base")
                    .child(getLocale())
                    .child(serialName)
                    .setValue(Utils.getDate());
            onInteraction();
        }
    }

    public static void renameSerial(Context context, Serial oldSerial, Serial newSerial) {
        deleteSerial(context, oldSerial);
        saveSerial(context, newSerial);
        onInteraction();
    }

    private static String getLocale() {
        return Locale.getDefault().getLanguage();
    }
}
