package by.wiskiw.serialsmanager.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import by.wiskiw.serialsmanager.R;
import by.wiskiw.serialsmanager.defaults.Constants;
import by.wiskiw.serialsmanager.edit.dialog.EditDialog;
import by.wiskiw.serialsmanager.objects.Serial;
import by.wiskiw.serialsmanager.storage.PreferencesHelper;

/**
 * Created by WiskiW on 25.12.2016.
 */

public class SerialListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements RecyclerViewChangeListener {

    private List<Serial> serialList;

    public SerialListAdapter(List<Serial> serialList) {
        this.serialList = serialList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.serial_item, parent, false);
        return new SerialHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SerialHolder serialHolder = (SerialHolder) holder;
        Serial serial = serialList.get(position);
        serialHolder.setSerial(serial);
        serialHolder.showData();
        serialHolder.setRecyclerViewChangeListener(this);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return serialList.size();
    }

    @Override
    public void onRemove(Context context, int pos, Serial serial) {
        serialList.remove(pos);
        notifyItemRemoved(pos);
        int size = serialList.size();
        notifyItemRangeChanged(pos, size);
    }

    @Override
    public void onUpdate(Context context, int pos, Serial serial) {
        serialList.set(pos, serial);
        notifyItemChanged(pos);
    }

    @Override
    public void onAdd(Context context, Serial serial) {
        if (PreferencesHelper.getShortingMethod(context).equals("alphabet_order")) {
            int index = getAlphabeticalIndex(serial);
            serialList.add(index, serial);
        } else {
            serialList.add(serial);
        }

        notifyDataSetChanged();
    }

    private int getAlphabeticalIndex(Serial serial) {
        int index = Collections.binarySearch(serialList, serial,
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

}

interface RecyclerViewChangeListener {
    void onRemove(Context context, int pos, Serial serial);

    void onUpdate(Context context, int pos, Serial serial);

    void onAdd(Context context, Serial serial);
}
