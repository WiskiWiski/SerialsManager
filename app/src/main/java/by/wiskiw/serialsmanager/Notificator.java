package by.wiskiw.serialsmanager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import by.wiskiw.serialsmanager.activities.MainActivity;
import by.wiskiw.serialsmanager.defaults.Constants;
import by.wiskiw.serialsmanager.objects.Serial;
import by.wiskiw.serialsmanager.receivers.AlarmBroadcastReceiver;
import by.wiskiw.serialsmanager.storage.PreferencesHelper;
import by.wiskiw.serialsmanager.storage.PreferencesStorage;
import by.wiskiw.serialsmanager.storage.json.JsonDatabase;

/**
 * Created by WiskiW on 07.12.2016.
 */

public class Notificator {

    public static void createNotification(Context context, Intent receiverIntent) {
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

    private static void setAlarm(Context context, Serial serial) {
        long timeMs = serial.getNextEpisodeDateMs();
        String timeStr = Utils.getDate(timeMs);
        Log.d(Constants.TAG, "Alarm set on " + timeStr + " - " + timeMs);
        Toast.makeText(context, "Alarm set on " + timeStr, Toast.LENGTH_LONG).show();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra("serial_name", serial.getName());
        intent.putExtra("episode", serial.getNextEpisode());
        intent.putExtra("season", serial.getNextSeason());
        intent.putExtra("notification_id", serial.getNotificationId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, serial.getNotificationId(), intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeMs, pendingIntent);
    }

    static void cancelAlarm(Context context, Serial serial) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Notificator.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, serial.getNotificationId(), intent, 0);
        alarmManager.cancel(pendingIntent);
        Log.d(Constants.TAG, "Alarms removed for " + serial.getName());
    }

    static void checkNotificationData(final Context context, Serial serial) {
        if (!PreferencesHelper.isNotificationsEnable(context)) return;

        if (serial.getIdentityLevel() == -1) {
            requestNotificationData(context, serial);
        } else if (serial.getIdentityLevel() < Constants.MIN_IDENTITY_LVL) {
            Log.d(Constants.TAG, "Identity level (" + serial.getIdentityLevel()
                    + "%) for '" + serial.getName() + "' is too low, alarm didn't created");
        } else if (serial.getNextEpisodeDateMs() == -1) {
            requestNotificationData(context, serial);
        } else {
            long nextEpDateMs = serial.getNextEpisodeDateMs();
            if (nextEpDateMs == 0) {
                // TODO: Не удалось получить дату напоминания
                Log.d(Constants.TAG, "Next episode date for '" + serial.getName() + "' not found.");
            } else if (Utils.isFutureTime(nextEpDateMs)) {
                serial.setNotificationId((int) (nextEpDateMs / 100000));
                int nextSeasonNum = serial.getNextSeason();
                int nextEpisodeNum = serial.getNextEpisode();
                Log.d(Constants.TAG, "Next episode of '" + serial.getName() +
                        "'(" + serial.getIdentityLevel() + "%) s" + nextSeasonNum + "e" + nextEpisodeNum);
                if (nextSeasonNum == 0 || nextEpisodeNum == 0) {
                    // TODO: Создать Alarm без номера серии
                    setAlarm(context, serial);
                } else {
                    // TODO: Создаем Alarm с номером серии и сезоном
                    int seasonNum = serial.getSeason();
                    int episodeNum = serial.getEpisode();
                    if (nextEpisodeNum != 1) {
                        if (nextSeasonNum == seasonNum && nextEpisodeNum == episodeNum + 1) {
                            setAlarm(context, serial);
                        }
                    } else if (nextSeasonNum == seasonNum + 1 || nextSeasonNum == seasonNum) {
                        // Следующая серия - начало сезона
                        setAlarm(context, serial);
                    }
                }
            } else {
                // Notification time in the past
                Log.d(Constants.TAG, "Notification time is out of date for '" + serial.getName() + "'. Try again...");
                serial.resetNotificationData();
                JsonDatabase.saveSerial(context, serial);
                requestNotificationData(context, serial);
            }
        }
    }

    private static void requestNotificationData(final Context context, final Serial serial) {
        Log.d(Constants.TAG, "Request for " + serial.getName());
        RequestQueue queue = Volley.newRequestQueue(context);
        final String url = "http://wiskiw.esy.es/sm/check_serial.php?serial=" + serial.getName();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        serial.setIdentityLevel(response.optInt("identity_lvl", 0));
                        if (serial.getIdentityLevel() >= Constants.MIN_IDENTITY_LVL) {
                            String showTime = response.optString("show_time", null);
                            //serial.setShowTime(response.optString("show_time", null));

                            String nextEpisodeDateString = response.optString("next_episode_date", null);
                            long nextEpisodeDateMs = parseDateString(nextEpisodeDateString, showTime);
                            serial.setNextEpisodeDateMs(nextEpisodeDateMs);
                            if (nextEpisodeDateMs != 0) {
                                serial.setNextSeason(response.optInt("next_season"));
                                serial.setNextEpisode(response.optInt("next_episode"));
                            }
                        }
                        JsonDatabase.saveSerial(context, serial);
                        checkNotificationData(context, serial);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constants.DEFAULT_REQUEST_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(getRequest);
    }

    private static long parseDateString(String dateString, String showTimeString) {
        if (dateString == null) {
            return 0;

        }

        // Dividing to array
        ArrayList<String> dateWords = new ArrayList<>();
        while (dateString.contains(" ")) {
            int spaceIndex = dateString.indexOf(" ");
            dateWords.add(dateString.substring(0, spaceIndex));
            dateString = dateString.substring(spaceIndex + 1);
        }
        dateWords.add(dateString);
        //dateWords: [24, января, 2017]


        // Getting day, mouth and year
        if (dateWords.size() == 3) {
            String day = null;
            String mouth = null;
            String year = null;

            for (int i = 0; i < 3; i++) {
                String word = dateWords.get(i);
                int length = word.length();

                if (length <= 2) {
                    if (length == 1) {
                        word = "0" + word;
                    }
                    // day
                    day = word;
                    //Log.d(Constants.TAG, "day: " + word);
                } else if (word.matches("(?i).*[a-zа-я].*")) {
                    // Mouth
                    mouth = getMouthNumber(word);
                    //Log.d(Constants.TAG, "mouth: " + word + " - " + word.matches("(?i).*[a-zа-я].*"));
                } else if (length == 4 && word.matches("[0-9]+")) {
                    //year
                    year = word;
                    //Log.d(Constants.TAG, "year: " + word + " - " + word.matches("[0-9]+"));
                }
            }


            //Generating Calendar object
            if (day != null || mouth != null || year != null) {

                // Getting episode show time
                if (showTimeString != null && showTimeString.contains(":")) {
                    int colonIndex = showTimeString.indexOf(":");
                    showTimeString = showTimeString.substring(colonIndex - 2, colonIndex + 3);
                } else {
                    showTimeString = "15:00";
                }

                try {
                    dateString = showTimeString + " " + day + "." + mouth + "." + year;

                    TimeZone timeZone = TimeZone.getTimeZone("Europe/Moscow");
                    DateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.ROOT);
                    dateFormat.setTimeZone(timeZone);
                    Calendar calendar = Calendar.getInstance(timeZone);
                    calendar.setTime(dateFormat.parse(dateString));

                    //Log.d(Constants.TAG, "UTC time:   " + calendar.getTimeInMillis());
                    //Log.d(Constants.TAG, "Local time: " + (calendar.getTimeInMillis() + TimeZone.getDefault().getRawOffset()));
                    return calendar.getTimeInMillis() + TimeZone.getDefault().getRawOffset();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    private static String getMouthNumber(String word) {
        word = word.toLowerCase();
        if (word.startsWith("ян")) {
            return "01";
        } else if (word.startsWith("фев")) {
            return "02";
        } else if (word.startsWith("март")) {
            return "03";
        } else if (word.startsWith("апр")) {
            return "04";
        } else if (word.startsWith("ма")) {
            return "05";
        } else if (word.startsWith("июн")) {
            return "06";
        } else if (word.startsWith("июл")) {
            return "07";
        } else if (word.startsWith("авг")) {
            return "08";
        } else if (word.startsWith("сент")) {
            return "09";
        } else if (word.startsWith("окт")) {
            return "10";
        } else if (word.startsWith("нояб")) {
            return "11";
        } else if (word.startsWith("дек")) {
            return "12";
        } else
            return null;
    }

}
