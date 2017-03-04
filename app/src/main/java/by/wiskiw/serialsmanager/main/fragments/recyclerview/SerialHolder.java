package by.wiskiw.serialsmanager.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import by.wiskiw.serialsmanager.Analytics;
import by.wiskiw.serialsmanager.Notificator;
import by.wiskiw.serialsmanager.R;
import by.wiskiw.serialsmanager.managers.AdManager;
import by.wiskiw.serialsmanager.objects.Serial;
import by.wiskiw.serialsmanager.storage.json.JsonDatabase;

/**
 * Created by WiskiW on 25.12.2016.
 */
class SerialHolder extends RecyclerView.ViewHolder {

    CardView cardView;
    TextView episodeView;
    TextView seasonView;
    TextView plusOne;
    TextView serialNameView;


    SerialHolder(View itemView) {
        super(itemView);
        cardView = (CardView) itemView.findViewById(R.id.card_view);
        serialNameView = (TextView) itemView.findViewById(R.id.serialName);
        episodeView = (TextView) itemView.findViewById(R.id.episodeTextView);
        seasonView = (TextView) itemView.findViewById(R.id.seasonTextView);
        plusOne = (TextView) itemView.findViewById(R.id.plus_one_btn);
    }

    void onPlusClick(Context context, Serial serial){
        JsonDatabase.saveSerial(context, serial);
        Analytics.sendPlusEpisodeEvent(context, serial);
        AdManager.plusOneClick(context);

        Notificator.checkNotificationData(context, serial);
        //SerialEditListener.registerEpisodeUpdate(context, serial);
        //SerialEditListener.registerEdit(context, serial);

    }

}