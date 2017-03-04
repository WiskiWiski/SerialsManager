package by.wiskiw.serialsmanager.main.fragments.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.NativeExpressAdView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import by.wiskiw.serialsmanager.R;
import by.wiskiw.serialsmanager.Utils;
import by.wiskiw.serialsmanager.defaults.Constants;
import by.wiskiw.serialsmanager.objects.Serial;
import by.wiskiw.serialsmanager.settings.SettingsHelper;

import static by.wiskiw.serialsmanager.defaults.Constants.TAG;

/**
 * Created by WiskiW on 25.12.2016.
 */

public class SerialListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int SERIAL_ITEM_VIEW_TYPE = 0;
    private static final int NATIVE_EXPRESS_AD_VIEW_TYPE = 1;

    private List<Serial> serialsList;
    private Context context;

    private NativeExpressAdView adView;
    private int adRow;

    private OnSerialLongClickListener onSerialLongClickListener;
    private OnSerialClickListener onSerialClickListener;

    public SerialListAdapter(Context context, List<Serial> serialsList) {
        this.context = context;
        this.serialsList = serialsList;
    }

    public void setAdView(NativeExpressAdView adView, int adRow) {
        this.adView = adView;
        this.adRow = adRow;
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
                        .inflate(R.layout.native_ad_item, parent, false);
                return new NativeExpressAdViewHolder(nativeExpressLayoutView);
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case SERIAL_ITEM_VIEW_TYPE:
                final SerialHolder serialHolder = (SerialHolder) holder;
                final Serial serial = serialsList.get(getPosWithoutAd(position));


                serialHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (onSerialLongClickListener != null) {
                            onSerialLongClickListener.onLongClick(serialHolder.getAdapterPosition(), serial);
                        }
                        return true;
                    }
                });

                serialHolder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onSerialClickListener != null) {
                            onSerialClickListener.onClick(serialHolder.getAdapterPosition(), serial);
                        }
                    }
                });

                serialHolder.plusOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        Context context = view.getContext();
                        Utils.vibrate(context, Constants.DEFAULT_VIBRATION);
                        serial.addEpisode();
                        serialHolder.plusClick(context, serial);
                        updateSerialView(serialHolder.getAdapterPosition(), serial);
                    }
                });

                serialHolder.plusOne.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View view) {
                        Context context = view.getContext();
                        serial.addSeason();
                        serialHolder.plusClick(context, serial);
                        updateSerialView(serialHolder.getAdapterPosition(), serial);
                        return true;
                    }
                });

                serialHolder.serialNameView.setText(serial.getName());
                serialHolder.episodeView.setText("e" + String.valueOf(serial.getEpisode()));
                serialHolder.seasonView.setText("s" + String.valueOf(serial.getSeason()));

                break;
            case NATIVE_EXPRESS_AD_VIEW_TYPE:
                NativeExpressAdViewHolder nativeExpressHolder = (NativeExpressAdViewHolder) holder;
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
        if (position == adRow) {
            return NATIVE_EXPRESS_AD_VIEW_TYPE;
        } else {
            return SERIAL_ITEM_VIEW_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        return getPosWithAd(serialsList.size() - 1) + 1; // считаем размер с учетом Ad-элемента
    }

    private void scrollTo(int pos) {
        RecyclerView recyclerView = (RecyclerView) ((Activity) context).findViewById(R.id.recycler_view);
        recyclerView.scrollToPosition(pos);
    }

    private int getPosWithAd(int pos) {
        // Вовзращает позицию в Recycler View с учетом Ad-элемента
        if (pos >= adRow) {
            pos++; // +1 if index after ad row
        }
        return pos;
    }

    private int getPosWithoutAd(int pos) {
        // Вовзращает позицию в Serial List вычетая Ad-элемент
        if (pos >= adRow) {
            pos--; // +1 if index after ad row
        }
        return pos;
    }

    public void addSerialView(Serial serial) {
        int pos = getAlphabeticalIndex(serial);
        int n = getItemCount();
        serialsList.add(pos, serial);
        //notifyItemInserted(getPosWithAd(pos));
        notifyItemRangeInserted(getPosWithAd(pos), getItemCount() - n);
        scrollTo(getPosWithAd(pos));
    }

    public void updateSerialView(int pos, Serial serial) {
        serialsList.set(getPosWithoutAd(pos), serial);
        notifyItemChanged(pos);
    }

    public void renameSerialView(int oldPos, Serial newSerial) {
        int newPos = getAlphabeticalIndex(newSerial);
        newPos = newPos == 0 ? 0 : newPos - 1;
        updateSerialView(getPosWithoutAd(oldPos), newSerial);
        serialsList.remove(getPosWithoutAd(oldPos));
        serialsList.add(newPos, newSerial);
        notifyItemMoved(oldPos, getPosWithAd(newPos));
        scrollTo(getPosWithAd(newPos));
    }

    public void removeSerialView(int pos) {
        int n = getItemCount();
        serialsList.remove(getPosWithoutAd(pos));
        if (n - getItemCount() > 1) {
            // Если удаилось 2 элемента
            notifyItemRangeRemoved(getPosWithoutAd(pos), 2);
        } else {
            notifyItemRangeRemoved(pos, 1);
        }
    }

    private int getAlphabeticalIndex(Serial serial) {
        if (SettingsHelper.getShortingMethod(context) == SettingsHelper.ShortingOrder.DATE
                || serialsList.size() < 1) {
            return 0;
        }

        int index = Collections.binarySearch(serialsList, serial, new Comparator<Serial>() {
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

    public void setOnSerialLongClickListener(OnSerialLongClickListener onSerialLongClickListener) {
        this.onSerialLongClickListener = onSerialLongClickListener;
    }

    public void setOnSerialClickListener(OnSerialClickListener onSerialClickListener) {
        this.onSerialClickListener = onSerialClickListener;
    }

    public interface OnSerialLongClickListener {
        void onLongClick(int position, Serial serial); // position - позиция в Recycler View (с учетом рекламы)
    }

    public interface OnSerialClickListener {
        void onClick(int position, Serial serial); // position - позиция в Recycler View (с учетом рекламы)
    }

}

