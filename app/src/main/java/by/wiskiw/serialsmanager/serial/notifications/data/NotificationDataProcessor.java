package by.wiskiw.serialsmanager.serial.notifications.data;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import by.wiskiw.serialsmanager.app.Constants;
import by.wiskiw.serialsmanager.serial.Serial;

/**
 * Created by WiskiW on 31.03.2017.
 */

public class NotificationDataProcessor {

    private static final String TAG = Constants.TAG + ":NotifDataProces";

    static long parseDateString(String dateString, String showTimeString) {
        // Return UTC time
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
                    //Log.d(TAG, "day: " + word);
                } else if (word.matches("(?i).*[a-zа-я].*")) {
                    // Mouth
                    mouth = getMouthNumber(word);
                    //Log.d(TAG, "mouth: " + word + " - " + word.matches("(?i).*[a-zа-я].*"));
                } else if (length == 4 && word.matches("[0-9]+")) {
                    //year
                    year = word;
                    //Log.d(TAG, "year: " + word + " - " + word.matches("[0-9]+"));
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

                    DateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.ROOT);
                    //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                    calendar.setTime(dateFormat.parse(dateString));

                    //Log.d(TAG, "UTC time:   " + calendar.getTimeInMillis());
                    //Log.d(TAG, "Local time: " + (calendar.getTimeInMillis() + TimeZone.getDefault().getRawOffset()));
                    //return calendar.getTimeInMillis() + TimeZone.getDefault().getRawOffset();
                    return calendar.getTimeInMillis();
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

    public static boolean isAlarmAvailable(Serial serial){
        int nextSeasonNum = serial.getNextSeason();
        int nextEpisodeNum = serial.getNextEpisode();

        if (nextSeasonNum == 0 || nextEpisodeNum == 0) {
            // Создать Alarm без номера серии [id=2]
            Log.d(TAG, "Available to create alarm for '" + serial.getName() + "'(" + serial.getIdentityLevel() + "%) without episode number.");
            return true;
        } else {
            // Создаем Alarm с номером серии и сезоном
            Log.i(TAG, "Next episode of '" + serial.getName() +
                    "'(" + serial.getIdentityLevel() + "%) s" + nextSeasonNum + "e" + nextEpisodeNum);
            int seasonNum = serial.getSeason();
            int episodeNum = serial.getEpisode();
            if (nextEpisodeNum != 1) {
                if (nextSeasonNum == seasonNum && nextEpisodeNum == episodeNum + 1) {
                    return true;
                }
            } else if (nextSeasonNum == seasonNum + 1 || nextSeasonNum == seasonNum) {
                // Следующая серия - начало сезона
                return true;
            }
        }
        return false;
    }

}
