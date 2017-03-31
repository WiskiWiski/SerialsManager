package by.wiskiw.serialsmanager.edit.fragment.tabs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import by.wiskiw.serialsmanager.R;
import by.wiskiw.serialsmanager.app.Utils;
import by.wiskiw.serialsmanager.defaults.Constants;
import by.wiskiw.serialsmanager.notifications.Notificator;
import by.wiskiw.serialsmanager.notifications.SAlarmManager;
import by.wiskiw.serialsmanager.objects.Serial;

/**
 * Created by WiskiW on 28.12.2016.
 */

public class InfoTabFragment extends Fragment implements Notificator.DataRequestListener {

    private static final String TAG = Constants.TAG + ":InfoTab";

    private static final String BUNDLE_SERIAL = "serial_tag";

    private Serial serial;

    private ImageView updateNotifDataBtn;
    private Switch notificationSwitcher;

    private LinearLayout dataContainer;
    private FrameLayout noDataContainer;
    private FrameLayout loadingDataContainer;

    private TextView identityTV;
    private TextView nextEpisodeTV;
    private TextView timeTV;
    private TextView statusTV;

    private boolean requestInProgress = false; // TODO : Move to Notificator class

    public InfoTabFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey(BUNDLE_SERIAL)) {
            serial = args.getParcelable(BUNDLE_SERIAL);
        } else {
            throw new IllegalArgumentException("Must be created through newInstance(...)");
        }
    }

    public static InfoTabFragment newInstance(Serial serial) {
        final InfoTabFragment fragment = new InfoTabFragment();
        final Bundle arguments = new Bundle();
        arguments.putParcelable(BUNDLE_SERIAL, serial);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_info_tab, container, false);
        initViews(rootView);

        if (serial != null) {
            if (serial.isNotificationsEnable()) {
                setMainData(serial);
                notificationSwitcher.setChecked(true);
            }
            notificationSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        setMainData(serial);
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                        sharedPreferences.edit()
                                .putBoolean(getString(R.string.pref_screen_key_new_episode_notification), true)
                                .apply();
                        updateNotificationData(serial);
                    } else {
                        showNoDataView();
                    }
                }
            });

            updateNotifDataBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notificationSwitcher.setChecked(true);
                    updateNotificationData(serial);
                }
            });
        } else {
            notificationSwitcher.setEnabled(false);
        }
        return rootView;
    }

    private void updateNotificationData(Serial serial) {
        if (!requestInProgress) {
            Context context = getContext();
            showLoadingView();
            SAlarmManager.cancelAlarm(context, serial);
            serial.resetNotificationData();
            requestInProgress = true;
            Notificator.checkNotificationData(context, serial, this);
        }
    }

    private void initViews(View rootView) {
        dataContainer = (LinearLayout) rootView.findViewById(R.id.data_container);
        noDataContainer = (FrameLayout) rootView.findViewById(R.id.no_data_container);
        loadingDataContainer = (FrameLayout) rootView.findViewById(R.id.loading_data_container);

        updateNotifDataBtn = (ImageView) rootView.findViewById(R.id.update_notif_data_btn);
        notificationSwitcher = (Switch) rootView.findViewById(R.id.notification_switcher);
        identityTV = (TextView) rootView.findViewById(R.id.identity);
        nextEpisodeTV = (TextView) rootView.findViewById(R.id.next_episode);
        timeTV = (TextView) rootView.findViewById(R.id.time);
        statusTV = (TextView) rootView.findViewById(R.id.status);
    }


    private void showLoadingView() {
        if (loadingDataContainer != null && dataContainer != null && noDataContainer != null) {
            loadingDataContainer.setEnabled(true);
            loadingDataContainer.setVisibility(View.VISIBLE);

            dataContainer.setEnabled(false);
            dataContainer.setVisibility(View.GONE);

            noDataContainer.setEnabled(false);
            noDataContainer.setVisibility(View.GONE);
        }
    }

    private void showNoDataView() {
        if (loadingDataContainer != null && dataContainer != null && noDataContainer != null) {
            loadingDataContainer.setEnabled(false);
            loadingDataContainer.setVisibility(View.GONE);

            dataContainer.setEnabled(false);
            dataContainer.setVisibility(View.GONE);

            noDataContainer.setEnabled(true);
            noDataContainer.setVisibility(View.VISIBLE);
        }
    }

    private void showMainDataView() {
        if (loadingDataContainer != null && dataContainer != null && noDataContainer != null) {
            Log.d(TAG, "showMainDataView: ");
            loadingDataContainer.setEnabled(false);
            loadingDataContainer.setVisibility(View.GONE);

            dataContainer.setEnabled(true);
            dataContainer.setVisibility(View.VISIBLE);

            noDataContainer.setEnabled(false);
            noDataContainer.setVisibility(View.GONE);
        }
    }

    private void setMainData(Serial serial) {
        if (serial.getNextEpisodeDateMs() <= 0) {
            updateNotificationData(serial);
        } else {
            showMainDataView();
            nextEpisodeTV.setText("s" + serial.getNextSeason() + "e" + serial.getNextEpisode());
            timeTV.setText(String.valueOf(Utils.getDate(serial.getNextEpisodeDateMs())));
            identityTV.setText(serial.getIdentityLevel() + "%");
        }
    }


    public boolean getNotificationSwitcher() {
        return notificationSwitcher == null || notificationSwitcher.isChecked();
    }

    @Override
    public void onSuccess(Serial serial) {
        Log.d(TAG, "onSuccess: ");
        setMainData(serial);
        requestInProgress = false;
    }

    @Override
    public void onFailed(int errId, String msg) {
        Log.d(TAG, "onFailed[" + errId + "]: " + msg);
        showNoDataView();
        requestInProgress = false;
    }
}
