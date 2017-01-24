package by.wiskiw.serialsmanager.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;

import java.util.ArrayList;
import java.util.List;

import by.wiskiw.serialsmanager.R;
import by.wiskiw.serialsmanager.Utils;
import by.wiskiw.serialsmanager.adapters.SerialListAdapter;
import by.wiskiw.serialsmanager.edit.dialog.AddDialogListener;
import by.wiskiw.serialsmanager.edit.dialog.EditDialog;
import by.wiskiw.serialsmanager.objects.Serial;
import by.wiskiw.serialsmanager.storage.json.JsonDatabase;

public class MainFragment extends Fragment {

    private SerialListAdapter serialListAdapter;
    private RecyclerView recyclerView;

    private final int NATIVE_EXPRESS_AD_HEIGHT = 120;
    private final String AD_UNIT_ID = "ca-app-pub-3940256099942544/1072772517";
    private final String MY_AD_UNIT_ID = "ca-app-pub-5135672707034508/4573968875";
    private final int adRow = 3;


    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Context context = rootView.getContext();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        fillRecyclerView(context);

        return rootView;
    }


    private void fillRecyclerView(Context context) {
        List<Serial> serialList = JsonDatabase.getSerials(context);
        if (serialList.size() == 0 && Utils.firstStart) {
            EditDialog.addingFirst(context, new AddDialogListener() {
                @Override
                public void onSave(Context context, Serial serial) {
                    fillRecyclerView(context);
                }
            });
        } else {
            List<Object> itemsList = new ArrayList<>();
            for (Object serial : serialList) {
                itemsList.add(serial);
            }

            final NativeExpressAdView adView = new NativeExpressAdView(context);
            itemsList.add(adRow, adView);
            setUpAndLoadNativeExpressAds(itemsList);

            serialListAdapter = new SerialListAdapter(itemsList);
            recyclerView.setAdapter(serialListAdapter);
        }
    }

    public void addSerial() {
        Context context = getContext();
        EditDialog.adding(context, new AddDialogListener() {
            @Override
            public void onSave(Context context, Serial serial) {
                if (serialListAdapter != null) {
                    serialListAdapter.onAdd(context, serial);
                }
            }
        });

    }



    private void setUpAndLoadNativeExpressAds(final List<Object> itemsList) {
        // Use a Runnable to ensure that the RecyclerView has been laid out before setting the
        // ad size for the Native Express ad. This allows us to set the Native Express ad's
        // width to match the full width of the RecyclerView.
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                final float scale = getContext().getResources().getDisplayMetrics().density;
                // Set the ad size and ad unit ID for each Native Express ad in the items list.
                final NativeExpressAdView adView = (NativeExpressAdView) itemsList.get(adRow);
                final CardView cardView = (CardView) getView().findViewById(R.id.dataCardView);
                final int adWidth = cardView.getWidth() - cardView.getPaddingLeft() - cardView.getPaddingRight();

                AdSize adSize = new AdSize((int) (adWidth / scale), NATIVE_EXPRESS_AD_HEIGHT);
                adView.setAdSize(adSize);
                adView.setAdUnitId(AD_UNIT_ID);

                // Load the Native Express ad.
                adView.loadAd(new AdRequest.Builder().build());
            }
        });
    }
}
