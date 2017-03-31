package by.wiskiw.serialsmanager.managers;

import android.content.Context;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import by.wiskiw.serialsmanager.app.App;
import by.wiskiw.serialsmanager.BuildConfig;
import by.wiskiw.serialsmanager.app.Constants;
import by.wiskiw.serialsmanager.storage.PreferencesStorage;

/**
 * Created by WiskiW on 23.10.2016.
 */

public class AdManager {

    private static final String TAG = Constants.TAG + ":AdManager";

    private static final String AD_TEST_UNIT_ID = "ca-app-pub-3940256099942544/1072772517";
    private static final String DELETE_ACTION_UNIT_ID = "ca-app-pub-5135672707034508/7704077670";
    private static final String PLUS_CLICK_UNIT_ID = "ca-app-pub-5135672707034508/3097251273";
    private static final String RECYCLER_VIEW_NATIVE_AD_UNIT_ID = "ca-app-pub-5135672707034508/4573968875";

    public static final String PREFERENCE_ADS_ENABLE = "ads_enable";
    private static final String PREFERENCE_LOCAL_ADS_ENABLE = "local_ads_enable";
    public static final String PREFERENCE_SHOW_AD_EVERY_CLICK_NUMB = "show_ad_every";
    public static final String PREFERENCE_AD_ROW_INDEX = "ad_row_index";
    public static final String PREFERENCE_WATCHED_EPISODES_COUNTER = "watched_episodes_counter";
    private static final boolean DEFAULT_VALUE_ADS_ENABLE = true;
    private static final boolean DEFAULT_VALUE_LOCAL_ADS_ENABLE = true;

    public static final int RECYCLER_VIEW_AD_HEIGHT = 150;
    private static final int DEFAULT_AD_ROW_INDEX = 6;
    private static final int DEFAULT_SHOW_AD_EVERY_CLICK_NUMB = 8;

    private static final String TEST_ID_NEXUS_5 = "57BEE3366E891C70D131E115EE2667B6";
    private static final String TEST_ID_GALAXY_NEXUS = "7923C98F4CEED9E71AC979DD1A212C74";
    private static final String TEST_ID_I9070 = "6096BCF16BFA2B9208A95443591F0569";

    private static int watchEpisodeCounter;
    private static int showAdEveryClickNumb;
    private static int adRowIndex;

    private static InterstitialAd deleteActionInterstitialAd;
    private static InterstitialAd plusClickInterstitialAd;
    private static boolean adsEnable = false;
    private static AdRequest adRequest;

    public static void prepareAd(Context context) {
        watchEpisodeCounter = -1;
        showAdEveryClickNumb = -1;
        adRowIndex = -1;

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

    public static String getRecyclerViewNativeAdUnitId() {
        if (BuildConfig.DEBUG) {
            return AD_TEST_UNIT_ID;
        } else {
            return RECYCLER_VIEW_NATIVE_AD_UNIT_ID;
        }
    }

    public static void disableAds(Context context) {
        adsEnable = false;
        PreferencesStorage.saveBoolean(context, PREFERENCE_LOCAL_ADS_ENABLE, false);
    }

    public static void enableAds(Context context) {
        adsEnable = true;
        PreferencesStorage.saveBoolean(context, PREFERENCE_LOCAL_ADS_ENABLE, true);
    }

    public static boolean isAdsEnable(Context context) {
        boolean remote = PreferencesStorage.getBoolean(context,
                PREFERENCE_ADS_ENABLE, DEFAULT_VALUE_ADS_ENABLE);
        boolean local = PreferencesStorage.getBoolean(context,
                PREFERENCE_LOCAL_ADS_ENABLE, DEFAULT_VALUE_LOCAL_ADS_ENABLE);
        // Log.d(Constants.TAG, "remote:" + String.valueOf(remote));
        // Log.d(Constants.TAG, "local:" + String.valueOf(local));
        // Log.d(Constants.TAG, "oldFagLvl:" + oldFagLvl);
        return remote && local && App.getInstallVersion(context) > 3;
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
                    .addTestDevice(TEST_ID_GALAXY_NEXUS)
                    .addTestDevice(TEST_ID_I9070)
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
        if (watchEpisodeCounter == -1) {
            watchEpisodeCounter = PreferencesStorage.getInt(context, PREFERENCE_WATCHED_EPISODES_COUNTER, 0);
        }
        return watchEpisodeCounter;
    }

    private static void addWatchedEpisode(Context context) {
        watchEpisodeCounter = getWatchedEpisodesCount(context) + 1;
        PreferencesStorage.saveInt(context, PREFERENCE_WATCHED_EPISODES_COUNTER, watchEpisodeCounter);
    }

    private static void resetWatchedCounter(Context context) {
        watchEpisodeCounter = 0;
        PreferencesStorage.saveInt(context, PREFERENCE_WATCHED_EPISODES_COUNTER, watchEpisodeCounter);
    }

    private static int getShowAdClickNumb(Context context) {
        // Количество кликов +1 для показа рекламы
        if (showAdEveryClickNumb == -1) {
            showAdEveryClickNumb = PreferencesStorage.getInt(context,
                    PREFERENCE_SHOW_AD_EVERY_CLICK_NUMB, DEFAULT_SHOW_AD_EVERY_CLICK_NUMB);
        }
        return showAdEveryClickNumb;
    }

    public static int getAdRowIndex(Context context) {
        // Индекс строки с рекламой
        if (adRowIndex == -1) {
            adRowIndex = PreferencesStorage.getInt(context,
                    PREFERENCE_AD_ROW_INDEX, DEFAULT_AD_ROW_INDEX);
        }
        return adRowIndex;
    }

    public static void plusOneClick(Context context) {
        int showAdEvery = getShowAdClickNumb(context);
        int watchedCount = getWatchedEpisodesCount(context);
        if (watchedCount >= showAdEvery) {
            resetWatchedCounter(context);
            showPlusClickAd();
        }
        addWatchedEpisode(context);
    }


}
