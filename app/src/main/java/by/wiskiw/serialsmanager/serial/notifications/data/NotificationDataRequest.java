package by.wiskiw.serialsmanager.serial.notifications.data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import by.wiskiw.serialsmanager.app.Constants;
import by.wiskiw.serialsmanager.app.Utils;
import by.wiskiw.serialsmanager.serial.Serial;
import by.wiskiw.serialsmanager.serial.notifications.SeAlarmManager;
import by.wiskiw.serialsmanager.storage.PreferencesStorage;

/**
 * Created by WiskiW on 31.03.2017.
 */

public class NotificationDataRequest {

    private static final String TAG = Constants.TAG + ":SeDataRequest";

    private static final int DEFAULT_REQUEST_TIMEOUT = 60000;
    private static final String URL_CHECK_SERIAL = "https://wiskiw.000webhostapp.com/sm/check_serial.php?serial=";
    //private static final String URL_CHECK_SERIAL = "https://wiskiw.000webhostapp.com/sm/check_serial_b1.php?serial=";


    private static RequestQueue requestQueue;
    private Context context;
    private Serial serial;
    private OnDataRequestListener callback;


    public NotificationDataRequest(Context context) {
        this.context = context;
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
    }

    public NotificationDataRequest resetNotificationData() {
        if (serial != null) {
            serial.resetNotificationData();
        } else {
            Log.e(TAG, "Call the resetNotificationData() after setSerial()!");
        }
        return this;
    }

    public NotificationDataRequest setDataRequestListener(OnDataRequestListener listener) {
        this.callback = listener;
        return this;
    }

    public NotificationDataRequest setSerial(Serial serial) {
        this.serial = serial;
        return this;
    }


    public NotificationDataRequest requestAlarm() {
        /*
            NotificationDataRequest automatically remove previews alarms and set new (if the request is success).
         */
        SeAlarmManager.cancelAlarm(context, serial);
        checkDataValidity();
        return this;
    }

    private void checkDataValidity() {
        //if (!SettingsHelper.isNotificationsEnable(context)) return;

        int minIdentityLvl = PreferencesStorage.getInt(context,
                Constants.PREFERENCE_MIN_IDENTITY_LVL, Constants.DEFAULT_MIN_IDENTITY_LVL);

        if (serial.getIdentityLevel() == -1) {
            requestNewData();
        } else if (serial.getIdentityLevel() < minIdentityLvl) {
            returnFailedCallback(ERR_UNKNOWN_SERIAL, "Identity level (" + serial.getIdentityLevel()
                    + "%) for '" + serial.getName() + "' is too low, alarm didn't set");
        } else if (serial.getNextEpisodeDateMs() == -1) {
            requestNewData();
        } else {
            long nextEpDateMs = serial.getNextEpisodeDateMs();
            if (nextEpDateMs == 0) {
                // Не удалось получить дату напоминания [id=1]
                returnFailedCallback(ERR_EP_NOT_FOUND, "Next episode date for '" + serial.getName() + "' not found.");
            } else if (Utils.isFutureTime(nextEpDateMs)) {
                Log.d(TAG, "Notification time for '" + serial.getName() + "' successfully received!");
                returnSuccessCallback(serial);
            } else {
                // Notification time in the past
                Log.d(TAG, "Notification time is out of date for '" + serial.getName() + "'. Trying to requestAlarm new...");
                serial.resetNotificationData();
                requestNewData();
            }
        }
    }


    private void requestNewData() {
        abort(); // отменит пердыдущий запрос для данного сериала
        try {
            final String url = URL_CHECK_SERIAL + URLEncoder.encode(serial.getName(), "UTF-8");
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            serial.setIdentityLevel(response.optInt("identity", 0));
                            int minIdentityLvl = PreferencesStorage.getInt(context,
                                    Constants.PREFERENCE_MIN_IDENTITY_LVL, Constants.DEFAULT_MIN_IDENTITY_LVL);
                            if (serial.getIdentityLevel() >= minIdentityLvl) {
                                String showTime = response.optString("show_time", null);
                                //serial.setShowTime(response.optString("show_time", null));

                                String nextEpisodeDateString = response.optString("next_episode_date", null);
                                long nextEpisodeDateMs =
                                        NotificationDataProcessor.parseDateString(nextEpisodeDateString, showTime);
                                serial.setNextEpisodeDateMs(nextEpisodeDateMs);
                                if (nextEpisodeDateMs != 0) {
                                    serial.setNextSeason(response.optInt("next_season"));
                                    serial.setNextEpisode(response.optInt("next_episode"));
                                }
                            }
                            checkDataValidity();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                            returnFailedCallback(ERR_JSON_EXCEPTION, error.toString());
                        }
                    }
            );
            jsonRequest.setTag(serial.getName());
            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                    DEFAULT_REQUEST_TIMEOUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonRequest);
        } catch (UnsupportedEncodingException e) {
            returnFailedCallback(ERR_URL_EXCEPTION, "Can't encode serial name (" + serial.getName() + ") to URL!");
        }
    }

    public void abort() {
        if (requestQueue != null) {
            requestQueue.cancelAll(serial.getName());
        }
    }



    public static final int ERR_EP_NOT_FOUND = 1; // Next episode date not found
    public static final int ERR_JSON_EXCEPTION = 3; // JsonObjectRequest: onErrorResponse (частенько ошибка подключения к сети)
    public static final int ERR_URL_EXCEPTION = 4; // Can't encode serial name to URL
    public static final int ERR_UNKNOWN_SERIAL = 5; // Identity level is too low, alarm didn't set

    private void returnSuccessCallback(Serial serial) {
        SeAlarmManager.setAlarm(context, serial);
        if (callback != null) {
            callback.onSuccess(serial);
        }
    }

    private void returnFailedCallback(int errCode, String msg) {
        Log.e(TAG, msg);
        if (callback != null) {
            callback.onFailed(errCode, msg);
        }
    }

    public interface OnDataRequestListener {
        void onSuccess(Serial serial);

        void onFailed(int errCode, String msg);
    }

}
