package by.wiskiw.serialsmanager.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import by.wiskiw.serialsmanager.R;
import by.wiskiw.serialsmanager.objects.Serial;
import by.wiskiw.serialsmanager.storage.PreferencesHelper;

/**
 * Created by WiskiW on 25.12.2016.
 */

public class SerialListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements RecyclerViewChangeListener {

    // A menu item view type.
    private static final int SERIAL_ITEM_VIEW_TYPE = 0;
    // The Native Express ad view type.
    private static final int NATIVE_EXPRESS_AD_VIEW_TYPE = 1;
    private List<Object> itemsList;

    public SerialListAdapter(List<Object> itemsList) {
        this.itemsList = itemsList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case SERIAL_ITEM_VIEW_TYPE:
                View itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.serial_item, parent, false);
                return new SerialHolder(itemView);
            case NATIVE_EXPRESS_AD_VIEW_TYPE:
                View nativeExpressLayoutView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.native_express_ad_container, parent, false);
                return new NativeExpressAdViewHolder(nativeExpressLayoutView);
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case SERIAL_ITEM_VIEW_TYPE:
                SerialHolder serialHolder = (SerialHolder) holder;
                Serial serial = (Serial) itemsList.get(position);
                serialHolder.setSerial(serial);
                serialHolder.showData();
                serialHolder.setRecyclerViewChangeListener(this);
                break;
            case NATIVE_EXPRESS_AD_VIEW_TYPE:
                NativeExpressAdViewHolder nativeExpressHolder = (NativeExpressAdViewHolder) holder;
                NativeExpressAdView adView = (NativeExpressAdView) itemsList.get(position);
                ViewGroup adCardView = (ViewGroup) nativeExpressHolder.itemView;
                // The NativeExpressAdViewHolder recycled by the RecyclerView may be a different
                // instance than the one used previously for this position. Clear the
                // NativeExpressAdViewHolder of any subviews in case it has a different
                // AdView associated with it, and make sure the AdView for this position doesn't
                // already have a parent of a different recycled NativeExpressAdViewHolder.
                if (adCardView.getChildCount() > 0) {
                    adCardView.removeAllViews();
                }
                if (adView.getParent() != null) {
                    ((ViewGroup) adView.getParent()).removeView(adView);
                }

                // Add the Native Express ad to the native express ad view.
                adCardView.addView(adView);
        }
    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (itemsList.get(position) instanceof Serial) {
            return SERIAL_ITEM_VIEW_TYPE;
        } else {
            return NATIVE_EXPRESS_AD_VIEW_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    @Override
    public void onRemove(Context context, int pos, Serial serial) {
        itemsList.remove(pos);
        notifyItemRemoved(pos);
        int size = itemsList.size();
        notifyItemRangeChanged(pos, size);
    }

    @Override
    public void onUpdate(Context context, int pos, Serial serial) {
        itemsList.set(pos, serial);
        notifyItemChanged(pos);
    }

    @Override
    public void onAdd(Context context, Serial serial) {
        /*
        if (PreferencesHelper.getShortingMethod(context).equals("alphabet_order")) {
            int index = getAlphabeticalIndex(serial);
            itemsList.add(index, serial);
        } else {
            itemsList.add(serial);
        }
        */
        itemsList.add(serial);
        notifyDataSetChanged();
    }

    /*
    private int getAlphabeticalIndex(Serial serial) {
        int index = Collections.binarySearch(itemsList, serial,
                new Comparator<Serial>() {
                    @Override
                    public int compare(Serial serial1, Serial serial2) {
                        return serial1.getName().compareToIgnoreCase(serial2.getName());
                    }

                });

        if (index < 0) {
            index = (index * -1) - 1;
        }
        return index;
    }
    */

}

interface RecyclerViewChangeListener {
    void onRemove(Context context, int pos, Serial serial);

    void onUpdate(Context context, int pos, Serial serial);

    void onAdd(Context context, Serial serial);
}
