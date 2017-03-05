package by.wiskiw.serialsmanager.storage.json;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import by.wiskiw.serialsmanager.managers.AdManager;
import by.wiskiw.serialsmanager.R;
import by.wiskiw.serialsmanager.objects.Serial;
import by.wiskiw.serialsmanager.defaults.Constants;
import by.wiskiw.serialsmanager.settings.SettingsHelper;
import by.wiskiw.serialsmanager.storage.PreferencesStorage;

/**
 * Created by WiskiW on 22.10.2016.
 */

public class JsonDatabase {

    private static final String TAG = Constants.TAG + ":JsonDatabase";

    public static boolean isSerialExist(Context context, Serial newSerial) {
        List<Serial> serialList = getSerials(context);
        String newSerialName = newSerial.getName().toLowerCase().trim();
        for (Serial serial : serialList) {
            if (serial.getName().toLowerCase().trim().equals(newSerialName)){
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
            serialJsonObject.put(Constants.JSON_TAG_EPISODE, serial.getEpisode());
            serialJsonObject.put(Constants.JSON_TAG_SEASON, serial.getSeason());
            serialJsonObject.put(Constants.JSON_TAG_EPS, serial.getEps());
            serialJsonObject.put(Constants.JSON_TAG_NOTE, serial.getNote());
            serialJsonObject.put(Constants.JSON_TAG_NEXT_SEASON, serial.getNextSeason());
            serialJsonObject.put(Constants.JSON_TAG_NEXT_EPISODE, serial.getNextEpisode());
            serialJsonObject.put(Constants.JSON_TAG_NEXT_EPISODE_DATE, serial.getNextEpisodeDateMs());
            serialJsonObject.put(Constants.JSON_TAG_IDENTITY_LVL, serial.getIdentityLevel());
            serialJsonObject.put(Constants.JSON_TAG_NOTIFICATION_ID, serial.getNotificationId());

            JSONObject mainJsonObject = PreferencesStorage.getJson(context);
            mainJsonObject.put(serial.getName(), serialJsonObject);
            PreferencesStorage.saveJson(context, mainJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private static void checkNameToDisableAds(Context context, String name) {
        if (name.equalsIgnoreCase(context.getString(R.string.super_secret_password))) {
            AdManager.disableAds(context);
        }
    }

    public static List<Serial> getSerials(Context context) {
        List<Serial> serials = new ArrayList<>();
        try {
            JSONObject jsonObject = PreferencesStorage.getJson(context);
            Iterator<String> serialsJson = jsonObject.keys();
            AdManager.enableAds(context);
            while (serialsJson.hasNext()) {
                String serialName = serialsJson.next();
                checkNameToDisableAds(context, serialName);

                JSONObject serialJsonObject = jsonObject.getJSONObject(serialName);
                Serial serial = new Serial(serialName);

                serial.setEpisode(getIntFromJson(serialJsonObject, Constants.JSON_TAG_EPISODE, 1));
                serial.setSeason(getIntFromJson(serialJsonObject, Constants.JSON_TAG_SEASON, 1));
                serial.setEps(getIntFromJson(serialJsonObject, Constants.JSON_TAG_EPS, 0));
                serial.setNote(getStringFromJson(serialJsonObject, Constants.JSON_TAG_NOTE, null));
                serial.setNextSeason(getIntFromJson(serialJsonObject, Constants.JSON_TAG_NEXT_SEASON, 0));
                serial.setNextEpisode(getIntFromJson(serialJsonObject, Constants.JSON_TAG_NEXT_EPISODE, 0));
                serial.setIdentityLevel(getIntFromJson(serialJsonObject, Constants.JSON_TAG_IDENTITY_LVL, -1));
                serial.setNotificationId(getIntFromJson(serialJsonObject, Constants.JSON_TAG_NOTIFICATION_ID, 0));

                long ms = getLongFromJson(serialJsonObject, Constants.JSON_TAG_NEXT_EPISODE_DATE, -1);
                serial.setNextEpisodeDateMs(ms);

                serials.add(serial);
            }


            if (SettingsHelper.getShortingMethod(context) == SettingsHelper.ShortingOrder.ALPHABET){
                serials = sortByAlphabet(serials);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return serials;
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
