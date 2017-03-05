package by.wiskiw.serialsmanager.app;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import by.wiskiw.serialsmanager.defaults.Constants;
import by.wiskiw.serialsmanager.objects.Serial;

/**
 * Created by WiskiW on 27.12.2016.
 */

public class Analytics {

    private static final String TAG = Constants.TAG + ":Analytics";

    private static FirebaseAnalytics mFirebaseAnalytics;

    private static void initAnalytics(Context context){
        if (mFirebaseAnalytics == null){
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        }
    }

    public static void sendPlusEpisodeEvent(Context context, Serial serial){
        initAnalytics(context);
        Bundle params = generateSerialBundle(serial);
        mFirebaseAnalytics.logEvent("plus_episode", params);
    }

    private static Bundle generateSerialBundle(Serial serial){
        Bundle bundle = new Bundle();
        bundle.putInt("episode", serial.getEpisode());
        bundle.putInt("season", serial.getSeason());
        bundle.putInt("identity", serial.getIdentityLevel());
        bundle.putString("name", serial.getName());
        return bundle;
    }

}
