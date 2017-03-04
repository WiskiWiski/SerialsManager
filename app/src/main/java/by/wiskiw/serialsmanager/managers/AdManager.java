package by.wiskiw.serialsmanager.managers;

import android.content.Context;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import by.wiskiw.serialsmanager.BuildConfig;
import by.wiskiw.serialsmanager.defaults.Constants;
import by.wiskiw.serialsmanager.storage.PreferencesStorage;


/**
 * Created by WiskiW on 23.10.2016.
 */

public class AdManager {

    private static final String AD_TEST_UNIT_ID = "ca-app-pub-3940256099942544/1072772517";
    private static final String DELETE_ACTION_UNIT_ID = "ca-app-pub-5135672707034508/7704077670";
    private static final String PLUS_CLICK_UNIT_ID = "ca-app-pub-5135672707034508/3097251273";
    private static final String RECYCLER_VIEW_NATIVE_AD_UNIT_ID = "ca-app-pub-5135672707034508/4573968875";

    public static final int RECYCLER_VIEW_AD_HEIGHT = 150;
    public static final int AD_ROW = 6;

    private static final String TEST_ID_NEXUS_5 = "8CB3A425BBC68D897028EBCE4C4BB2FB";
    private static final String TEST_ID_GALAXY_NEXUS = "7923C98F4CEED9E71AC979DD1A212C74";

    private static int watchCounter;
    private static int showAdEvery;
    private static InterstitialAd deleteActionInterstitialAd;
    private static InterstitialAd plusClickInterstitialAd;
    private static boolean adsEnable = false;
    private static AdRequest adRequest;

    public static void prepareAd(Context context) {
        watchCounter = -1;
        showAdEvery = -1;
        adsEnable = isAdsEnable(context);
        if (adsEnable) {
            deleteActionInterstitialAd = new InterstitialAd(context);
            deleteActionInterstitialAd.setAdUnitId(DELETE_ACTION_UNIT_ID);
            requestNewInterstitial(deleteActionInterstitialAd);

            plusClickInterstitialAd = new InterstitialAd(context);
            plusClickInterstitialAd.setAdUnitId(PLUS_CLICK_UNIT_ID);
            requestNewInterstitial(plusClickInterstitialAd);
        }
    }

    public static String getRecyclerViewNativeAdUnitId(){
        if (BuildConfig.DEBUG) {
            return AD_TEST_UNIT_ID;
        } else {
            return RECYCLER_VIEW_NATIVE_AD_UNIT_ID;
        }
    }

    public static void disableAds(Context context) {
        adsEnable = false;
        PreferencesStorage.saveBoolean(context, Constants.PREFERENCE_LOCAL_ADS_ENABLE, false);
    }

    public static void enableAds(Context context) {
        adsEnable = true;
        PreferencesStorage.saveBoolean(context, Constants.PREFERENCE_LOCAL_ADS_ENABLE, true);
    }

    public static boolean isAdsEnable(Context context) {
        boolean remote = PreferencesStorage.getBoolean(context,
                Constants.PREFERENCE_ADS_ENABLE, Constants.DEFAULT_VALUE_ADS_ENABLE);
        boolean local = PreferencesStorage.getBoolean(context,
                Constants.PREFERENCE_LOCAL_ADS_ENABLE, Constants.DEFAULT_VALUE_LOCAL_ADS_ENABLE);
        int oldFagLvl = PreferencesStorage.getInt(context,
                Constants.PREFERENCE_OLD_FAG_LVL, Constants.DEFAULT_VALUE_OLD_FAG_LVL);
        // Log.d(Constants.TAG, "remote:" + String.valueOf(remote));
        // Log.d(Constants.TAG, "local:" + String.valueOf(local));
        // Log.d(Constants.TAG, "oldFagLvl:" + oldFagLvl);
        return remote && local && oldFagLvl > 3;
    }

    private static void requestNewInterstitial(final InterstitialAd interstitialAd) {
        generateAdRequest();
        interstitialAd.loadAd(adRequest);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial(interstitialAd);
            }
        });
    }

    private static void generateAdRequest() {
        if (adRequest == null) {
            adRequest = new AdRequest.Builder()
                    .addTestDevice(TEST_ID_NEXUS_5)
                    .addTestDevice(TEST_ID_GALAXY_NEXUS) // nexus 3
                    .build();
        }
    }

    public static void showDeleteActionAd() {
        if (adsEnable && deleteActionInterstitialAd != null && deleteActionInterstitialAd.isLoaded()) {
            deleteActionInterstitialAd.show();
            requestNewInterstitial(deleteActionInterstitialAd);
        }
    }

    private static void showPlusClickAd() {
        if (adsEnable && plusClickInterstitialAd != null && plusClickInterstitialAd.isLoaded()) {
            plusClickInterstitialAd.show();
            requestNewInterstitial(plusClickInterstitialAd);
        }
    }

    private static int getWatchedEpisodesCount(Context context) {
        if (watchCounter == -1) {
            watchCounter = PreferencesStorage.getInt(context, Constants.PREFERENCE_WATCHED_EPISODES_COUNTER, 0);
        }
        return watchCounter;
    }

    private static void addWatchedEpisode(Context context) {
        watchCounter = getWatchedEpisodesCount(context) + 1;
        PreferencesStorage.saveInt(context, Constants.PREFERENCE_WATCHED_EPISODES_COUNTER, watchCounter);
    }

    private static void resetWatchedCounter(Context context) {
        watchCounter = 0;
        PreferencesStorage.saveInt(context, Constants.PREFERENCE_WATCHED_EPISODES_COUNTER, watchCounter);
    }

    private static int showAdEvery(Context context) {
        if (showAdEvery == -1) {
            showAdEvery = PreferencesStorage.getInt(context,
                    Constants.PREFERENCE_SHOW_AD_EVERY, Constants.DEFAULT_VALUE_SHOW_AD_EVERY);
        }
        return showAdEvery;
    }

    public static void plusOneClick(Context context) {
        int showAdEvery = showAdEvery(context);
        int watchedCount = getWatchedEpisodesCount(context);
        if (watchedCount >= showAdEvery) {
            resetWatchedCounter(context);
            showPlusClickAd();
        }
        addWatchedEpisode(context);
    }



}
