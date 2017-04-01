package by.wiskiw.serialsmanager.storage.json;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import by.wiskiw.serialsmanager.app.Constants;
import by.wiskiw.serialsmanager.serial.Serial;
import by.wiskiw.serialsmanager.settings.SettingsHelper;
import by.wiskiw.serialsmanager.storage.PreferencesStorage;

/**
 * Created by WiskiW on 22.10.2016.
 */

public class JsonDatabase {

    private static final String TAG = Constants.TAG + ":JsonDatabase";

    private static final String JSON_TAG_EPISODE = "episode";
    private static final String JSON_TAG_SEASON = "season";
    private static final String JSON_TAG_EPS = "eps";
    private static final String JSON_TAG_NOTE = "note";
    private static final String JSON_TAG_NEXT_SEASON = "next_season";
    private static final String JSON_TAG_NEXT_EPISODE = "next_episode";
    private static final String JSON_TAG_NEXT_EPISODE_DATE = "next_episode_date";
    private static final String JSON_TAG_IDENTITY_LVL = "identity_lvl";
    private static final String JSON_TAG_NOTIFICATIONS_ENABLE = "notifications_enable";

    public static boolean isSerialExist(Context context, Serial newSerial) {
        List<Serial> serialList = getSerials(context);
        String newSerialName = newSerial.getName().toLowerCase().trim();
        for (Serial serial : serialList) {
            if (serial.getName().toLowerCase().trim().equals(newSerialName)) {
                return true;
            }
        }
        return false;
    }

    public static void deleteSerial(Context context, Serial serial) {
        JSONObject jsonObject = PreferencesStorage.getJson(context);
        jsonObject.remove(serial.getName());
        PreferencesStorage.saveJson(context, jsonObject);
    }

    public static void saveSerial(Context context, Serial serial) {
        try {
            JSONObject serialJsonObject = new JSONObject(); // we need another object to store the
            serialJsonObject.put(JSON_TAG_EPISODE, serial.getEpisode());
            serialJsonObject.put(JSON_TAG_SEASON, serial.getSeason());
            serialJsonObject.put(JSON_TAG_EPS, serial.getEps());
            serialJsonObject.put(JSON_TAG_NOTE, serial.getNote());
            serialJsonObject.put(JSON_TAG_NEXT_SEASON, serial.getNextSeason());
            serialJsonObject.put(JSON_TAG_NEXT_EPISODE, serial.getNextEpisode());
            serialJsonObject.put(JSON_TAG_NEXT_EPISODE_DATE, serial.getNextEpisodeDateMs());
            serialJsonObject.put(JSON_TAG_IDENTITY_LVL, serial.getIdentityLevel());
            serialJsonObject.put(JSON_TAG_NOTIFICATIONS_ENABLE, serial.isNotificationsEnable());

            JSONObject mainJsonObject = PreferencesStorage.getJson(context);
            mainJsonObject.put(serial.getName(), serialJsonObject);
            PreferencesStorage.saveJson(context, mainJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static List<Serial> getSerials(Context context) {
        JSONObject jsonObject = PreferencesStorage.getJson(context);
        List<Serial> serials = parseJsonObject(jsonObject);
        if (serials != null) {
            if (SettingsHelper.getShortingMethod(context) == SettingsHelper.ShortingOrder.ALPHABET) {
                serials = sortByAlphabet(serials);
            }
            return serials;
        } else {
            return new ArrayList<>();
        }
    }

    public static List<Serial> parseJsonObject(JSONObject jsonObject) {
        if (jsonObject == null) return null;
        try {
            List<Serial> serials = new ArrayList<>();
            Iterator<String> serialsJson = jsonObject.keys();
            while (serialsJson.hasNext()) {
                String serialName = serialsJson.next();

                JSONObject serialJsonObject = jsonObject.getJSONObject(serialName);
                Serial serial = new Serial(serialName);

                serial.setEpisode(getIntFromJson(serialJsonObject, JSON_TAG_EPISODE, 1));
                serial.setSeason(getIntFromJson(serialJsonObject, JSON_TAG_SEASON, 1));
                serial.setEps(getIntFromJson(serialJsonObject, JSON_TAG_EPS, 0));
                serial.setNote(getStringFromJson(serialJsonObject, JSON_TAG_NOTE, null));
                serial.setNextSeason(getIntFromJson(serialJsonObject, JSON_TAG_NEXT_SEASON, 0));
                serial.setNextEpisode(getIntFromJson(serialJsonObject, JSON_TAG_NEXT_EPISODE, 0));
                serial.setIdentityLevel(getIntFromJson(serialJsonObject, JSON_TAG_IDENTITY_LVL, -1));
                serial.enableNotifications(getBoolFromJson(serialJsonObject, JSON_TAG_NOTIFICATIONS_ENABLE, true));

                long ms = getLongFromJson(serialJsonObject, JSON_TAG_NEXT_EPISODE_DATE, -1);
                serial.setNextEpisodeDateMs(ms);

                serials.add(serial);
            }
            return serials;
        } catch (JSONException e) {
            Log.e(TAG, "parseJsonString: could't parse the JSONObject to Serials List!", e);
            return null;
        }
    }

    private static List<Serial> sortByAlphabet(List<Serial> list) {
        Collections.sort(list, new Comparator<Serial>() {
            @Override
            public int compare(Serial serial1, Serial serial2) {
                return serial1.getName().compareTo(serial2.getName());
            }
        });
        return list;
    }

    private static String getStringFromJson(JSONObject jsonObject, String dataTag, String defaultValue) {
        try {
            return jsonObject.getString(dataTag);
        } catch (JSONException e) {
            //e.printStackTrace();
            return defaultValue;
        }
    }

    private static int getIntFromJson(JSONObject jsonObject, String dataTag, int defaultValue) {
        try {
            return jsonObject.getInt(dataTag);
        } catch (JSONException e) {
            //e.printStackTrace();
            return defaultValue;
        }
    }

    private static long getLongFromJson(JSONObject jsonObject, String dataTag, long defaultValue) {
        try {
            return jsonObject.getLong(dataTag);
        } catch (JSONException e) {
            //e.printStackTrace();
            return defaultValue;
        }
    }

    private static boolean getBoolFromJson(JSONObject jsonObject, String dataTag, boolean defaultValue) {
        try {
            return jsonObject.getBoolean(dataTag);
        } catch (JSONException e) {
            //e.printStackTrace();
            return defaultValue;
        }
    }

    public static void renameSerial(Context context, Serial oldSerial, Serial newSerial) {
        JSONObject jsonObject = PreferencesStorage.getJson(context);
        try {
            String oldSerialName = oldSerial.getName();
            String newSerialName = newSerial.getName();

            jsonObject.put(newSerialName, jsonObject.remove(oldSerialName));

            PreferencesStorage.saveJson(context, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
