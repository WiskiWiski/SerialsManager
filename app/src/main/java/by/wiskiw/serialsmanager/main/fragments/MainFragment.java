package by.wiskiw.serialsmanager.main.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;

import java.util.List;

import by.wiskiw.serialsmanager.Notificator;
import by.wiskiw.serialsmanager.R;
import by.wiskiw.serialsmanager.Utils;
import by.wiskiw.serialsmanager.main.fragments.recyclerview.SerialListAdapter;
import by.wiskiw.serialsmanager.defaults.Constants;
import by.wiskiw.serialsmanager.edit.fragment.SerialEditFragment;
import by.wiskiw.serialsmanager.managers.AdManager;
import by.wiskiw.serialsmanager.objects.Serial;
import by.wiskiw.serialsmanager.storage.FirebaseDatabase;
import by.wiskiw.serialsmanager.storage.json.JsonDatabase;

public class MainFragment extends Fragment {

    private static final String TAG = Constants.TAG;

    private NativeExpressAdView adView;
    private SerialListAdapter serialListAdapter;

    private RecyclerView recyclerView;


    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Context context = getContext();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        fillRecyclerView(context);

        return rootView;
    }


    private void fillRecyclerView(final Context context) {
        List<Serial> serialList = JsonDatabase.getSerials(context);
        if (serialList.size() == 0 && Utils.firstStart) {
            // If serial list is empty
            addNewSerial();
        } else {
            serialListAdapter = new SerialListAdapter(context, serialList);
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
                                Notificator.checkNotificationData(context, newSerial);
                            }

                        }
                    });
                    serialEditFragment.setOnDeleteListener(new SerialEditFragment.OnDeleteListener() {
                        @Override
                        public void onDelete(Serial serial) {
                            serialListAdapter.removeSerialView(position);
                            JsonDatabase.deleteSerial(context, serial);
                            FirebaseDatabase.deleteSerial(context, serial);
                        }
                    });
                }
            });
            recyclerView.setAdapter(serialListAdapter);
            if (AdManager.isAdsEnable(context)) {
                setUpAndLoadNativeExpressAds(context);
            }
        }
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
                    Notificator.checkNotificationData(context, serial);
                }
            }
        });
    }

    private void setUpAndLoadNativeExpressAds(Context context) {
        adView = new NativeExpressAdView(context);
        serialListAdapter.setAdView(adView, AdManager.AD_ROW);
        // Use a Runnable to ensure that the RecyclerView has been laid out before setting the
        // ad size for the Native Express ad. This allows us to set the Native Express ad's
        // width to match the full width of the RecyclerView.
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                final float scale = getContext().getResources().getDisplayMetrics().density;
                final CardView cardView = (CardView) recyclerView.findViewById(R.id.card_view);
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
