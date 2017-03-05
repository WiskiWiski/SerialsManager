package by.wiskiw.serialsmanager.rate;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import by.wiskiw.serialsmanager.R;
import by.wiskiw.serialsmanager.defaults.Constants;


/**
 * Created by WiskiW on 27.01.2017.
 */

public class RateDialog {

    private static final String TAG = Constants.TAG + ":RateDialog";

    public static void createDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater factory = LayoutInflater.from(context);
        final View dialogView = factory.inflate(R.layout.rate_dialog, null);
        final EditText editText = (EditText) dialogView.findViewById(R.id.rateComment);
        final RatingBar ratingBar = (RatingBar) dialogView.findViewById(R.id.ratingBar);


        builder.setTitle(context.getString(R.string.rate_dialog_title));
        builder.setView(dialogView)
                .setPositiveButton(context.getString(R.string.rate_dialog_rate_button), null)
                .setNeutralButton(context.getString(R.string.rate_dialog_cancel_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });


        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final Button rateButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                rateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        float rate = ratingBar.getRating();
                        String msg = editText.getText().toString().trim();
                        sendEmail(context, rate, msg);

                        alertDialog.cancel();
                    }
                });
                rateButton.setVisibility(View.GONE);
                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rate, boolean b) {
                        ratingBar.setIsIndicator(true);
                        if (rate >= 4) {
                            editText.setVisibility(View.GONE);
                            rateButton.setVisibility(View.GONE);
                            rateThisApp(context);

                            alertDialog.cancel();
                        } else {
                            editText.setVisibility(View.VISIBLE);
                            rateButton.setVisibility(View.VISIBLE);
                        }
                    }
                });

            }
        });
        alertDialog.show();

    }

    private static void rateThisApp(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static void sendEmail(Context context, float rate, String message) {
        String topic = context.getString(R.string.rate_dialog_msg_topic);
        String uriText = "mailto:" + Constants.DEFAULT_VALUE_CONTACT_EMAIL +
                "?subject=" + Uri.encode(topic + " " + rate) +
                "&body=" + Uri.encode(message);
        Uri uri = Uri.parse(uriText);

        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
        sendIntent.setData(uri);
        String sendTitle = context.getString(R.string.rate_dialog_send_activity_title);
        context.startActivity(Intent.createChooser(sendIntent, sendTitle));
    }


}
