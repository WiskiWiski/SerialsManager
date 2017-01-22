package by.wiskiw.serialsmanager.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import by.wiskiw.serialsmanager.Analytics;
import by.wiskiw.serialsmanager.managers.AdManager;
import by.wiskiw.serialsmanager.edit.dialog.EditDialog;
import by.wiskiw.serialsmanager.edit.dialog.EditDialogListener;
import by.wiskiw.serialsmanager.R;
import by.wiskiw.serialsmanager.SerialsActions;
import by.wiskiw.serialsmanager.objects.Serial;
import by.wiskiw.serialsmanager.Utils;
import by.wiskiw.serialsmanager.defaults.Constants;
import by.wiskiw.serialsmanager.storage.json.JsonDatabase;

/**
 * Created by WiskiW on 25.12.2016.
 */
class SerialHolder extends RecyclerView.ViewHolder {
    private Serial serial;
    private TextView episodeView;
    private TextView seasonView;
    private TextView plusOne;
    private TextView serialNameView;
    private RecyclerViewChangeListener recyclerViewChangeListener;

    public void setSerial(Serial serial) {
        this.serial = serial;
    }

    void showData() {
        serialNameView.setText(serial.getName());
        episodeView.setText("e" + String.valueOf(serial.getEpisode()));
        seasonView.setText("s" + String.valueOf(serial.getSeason()));
    }

    void setRecyclerViewChangeListener(RecyclerViewChangeListener recyclerViewChangeListener) {
        this.recyclerViewChangeListener = recyclerViewChangeListener;
    }

    private void initViews(View rootView) {
        serialNameView = (TextView) rootView.findViewById(R.id.serialName);
        episodeView = (TextView) rootView.findViewById(R.id.episodeTextView);
        seasonView = (TextView) rootView.findViewById(R.id.seasonTextView);
        plusOne = (TextView) rootView.findViewById(R.id.plus_one_btn);
    }

    SerialHolder(View itemView) {
        super(itemView);
        initViews(itemView);

        plusOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Context context = view.getContext();
                Utils.vibrate(context, Constants.DEFAULT_VIBRATION);

                serial.addEpisode();
                onPlusClick(context);
            }
        });

        plusOne.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                Context context = view.getContext();
                serial.addSeason();
                onPlusClick(context);
                return true;
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Context context = view.getContext();
                EditDialog.editing(context, serial, new EditDialogListener() {
                    @Override
                    public void onSave(Context context, Serial serial) {
                        JsonDatabase.saveSerial(context, serial);
                        recyclerViewChangeListener.onUpdate(context, getAdapterPosition(), serial);
                    }

                    @Override
                    public void onDelete(Context context, Serial serial) {
                        JsonDatabase.deleteSerial(context, serial);
                        recyclerViewChangeListener.onRemove(context, getAdapterPosition(), serial);
                    }
                });
                return true;
            }
        });
    }

    private void onPlusClick(Context context){
        JsonDatabase.saveSerial(context, serial);
        Analytics.sendPlusEpisodeEvent(context, serial);
        AdManager.plusOneClick(context);
        recyclerViewChangeListener.onUpdate(context, getAdapterPosition(), serial);
        SerialsActions.onEpisodeUpdate(context, serial);
        SerialsActions.onEdit(context, serial);

    }

}