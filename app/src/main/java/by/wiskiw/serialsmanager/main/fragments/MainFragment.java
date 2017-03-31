package by.wiskiw.serialsmanager.main.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;

import java.util.List;

import by.wiskiw.serialsmanager.R;
import by.wiskiw.serialsmanager.app.App;
import by.wiskiw.serialsmanager.app.Constants;
import by.wiskiw.serialsmanager.edit.fragment.SerialEditFragment;
import by.wiskiw.serialsmanager.main.fragments.recyclerview.SerialListAdapter;
import by.wiskiw.serialsmanager.managers.AdManager;
import by.wiskiw.serialsmanager.serial.notifications.SeAlarmManager;
import by.wiskiw.serialsmanager.serial.Serial;
import by.wiskiw.serialsmanager.serial.notifications.data.NotificationDataRequest;
import by.wiskiw.serialsmanager.storage.FirebaseDatabase;
import by.wiskiw.serialsmanager.storage.json.JsonDatabase;

public class MainFragment extends Fragment {

    private static final String TAG = Constants.TAG + ":MainFrg";
    public static final String F_TAG = "main_fragment_tag";

    private NativeExpressAdView adView;
    private SerialListAdapter serialListAdapter;

    private FrameLayout emptyDataContainer;
    private RecyclerView recyclerView;


    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Context context = getContext();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        emptyDataContainer = (FrameLayout) rootView.findViewById(R.id.empty_data_container);
        emptyDataContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewSerial();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        fillRecyclerView(context);

        return rootView;
    }

    private void fillRecyclerView(final Context context) {
        List<Serial> serialList = JsonDatabase.getSerials(context);
        serialListAdapter = new SerialListAdapter(context);
        serialListAdapter.setOnSizeChangedListener(new SerialListAdapter.OnSizeChangedListener() {
            @Override
            public void onSizeChanged(int recyclerViewSize) {
                if (recyclerViewSize > 0) {
                    emptyDataContainer.setVisibility(View.GONE);
                    emptyDataContainer.setClickable(false);
                } else {
                    if (App.isFirstStart()) {
                        addNewSerial();
                    }
                    emptyDataContainer.setVisibility(View.VISIBLE);
                    emptyDataContainer.setClickable(true);
                }
            }
        });
        serialListAdapter.setSerialsList(serialList);
        serialListAdapter.setOnSerialLongClickListener(new SerialListAdapter.OnSerialLongClickListener() {
            @Override
            public void onLongClick(final int position, Serial serial) {
                String title = context.getString(R.string.dialog_title_edit);
                SerialEditFragment serialEditFragment =
                        SerialEditFragment.show(getChildFragmentManager(), title, serial);
                serialEditFragment.setOnEditListener(new SerialEditFragment.OnEditListener() {
                    @Override
                    public void onEdit(Serial oldSerial, Serial newSerial) {
                        boolean requestNotifData = false;
                        if (!oldSerial.getName().equals(newSerial.getName())) {
                            // Serial renamed
                            JsonDatabase.renameSerial(context, oldSerial, newSerial);
                            FirebaseDatabase.renameSerial(context, oldSerial, newSerial);
                            serialListAdapter.renameSerialView(position, newSerial);
                            newSerial.resetNotificationData();
                            requestNotifData = true;
                        } else {
                            JsonDatabase.saveSerial(context, newSerial);
                            FirebaseDatabase.saveSerial(context, newSerial);
                            serialListAdapter.updateSerialView(position, newSerial);
                        }
                        if (oldSerial.getEpisode() != newSerial.getEpisode() ||
                                oldSerial.getSeason() != newSerial.getSeason()) {
                            // Serial's episode/season changed
                            requestNotifData = true;
                        }
                        if (requestNotifData) {
                            requestNotificationData(newSerial);
                            //Notificator.checkNotificationData(context, newSerial, null);
                        }

                    }
                });
                serialEditFragment.setOnDeleteListener(new SerialEditFragment.OnDeleteListener() {
                    @Override
                    public void onDelete(Serial serial) {
                        serialListAdapter.removeSerialView(position);
                        JsonDatabase.deleteSerial(context, serial);
                        FirebaseDatabase.deleteSerial(context, serial);
                        SeAlarmManager.cancelAlarm(context, serial);
                        AdManager.showDeleteActionAd();
                    }
                });

            }
        });
        recyclerView.setAdapter(serialListAdapter);
        if (AdManager.isAdsEnable(context)) {
            setUpAndLoadNativeExpressAds(context);
        }
    }

    private void requestNotificationData(Serial serial){
        Context context = getContext();
        new NotificationDataRequest(context)
                .setDataRequestListener(new NotificationDataRequest.OnDataRequestListener() {
                    @Override
                    public void onSuccess(Serial serial) {
                        Context context = getContext();
                        JsonDatabase.saveSerial(context, serial);
                    }

                    @Override
                    public void onFailed(int errCode, String msg) {

                    }
                })
                .setSerial(serial).requestAlarm();
    }


    public void addNewSerial() {
        String title = getString(R.string.dialog_title_add);
        SerialEditFragment serialEditFragment =
                SerialEditFragment.show(getChildFragmentManager(), title, null);

        serialEditFragment.setOnCreateListener(new SerialEditFragment.OnCreateListener() {
            @Override
            public void onCreate(Serial serial) {
                if (serialListAdapter != null) {
                    Context context = getContext();
                    JsonDatabase.saveSerial(context, serial);
                    FirebaseDatabase.saveSerial(context, serial);
                    serialListAdapter.addSerialView(serial);
                    requestNotificationData(serial);
                    //Notificator.checkNotificationData(context, serial, null);
                }
            }
        });
    }


    private void setUpAndLoadNativeExpressAds(Context context) {
        adView = new NativeExpressAdView(context);
        serialListAdapter.setAdView(adView, AdManager.getAdRowIndex(context));
        // Use a Runnable to ensure that the RecyclerView has been laid out before setting the
        // ad size for the Native Express ad. This allows us to set the Native Express ad's
        // width to match the full width of the RecyclerView.
        final float scale = getContext().getResources().getDisplayMetrics().density;
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                final CardView cardView = (CardView) recyclerView.findViewById(R.id.card_view);
                if (cardView == null) return;
                final int adWidth = cardView.getWidth() - cardView.getPaddingLeft() - cardView.getPaddingRight();
                AdSize adSize = new AdSize((int) (adWidth / scale), AdManager.RECYCLER_VIEW_AD_HEIGHT);
                adView.setAdSize(adSize);
                adView.setAdUnitId(AdManager.getRecyclerViewNativeAdUnitId());

                // Load the Native Express ad.
                adView.loadAd(new AdRequest.Builder().build());
            }
        });
    }
}
