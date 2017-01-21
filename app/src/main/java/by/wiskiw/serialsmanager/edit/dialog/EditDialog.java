package by.wiskiw.serialsmanager.edit.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import by.wiskiw.serialsmanager.Utils;
import by.wiskiw.serialsmanager.managers.AdManager;
import by.wiskiw.serialsmanager.R;
import by.wiskiw.serialsmanager.SerialsActions;
import by.wiskiw.serialsmanager.objects.Serial;
import by.wiskiw.serialsmanager.storage.json.JsonDatabase;

/**
 * Created by WiskiW on 25.12.2016.
 */

public class EditDialog {

    private static AddDialogListener addDialogListener;
    private static EditDialogListener editDialogListener;

    public static void adding(Context context, AddDialogListener addDialogListener) {
        String title = context.getString(R.string.dialog_title_add);
        EditDialog.addDialogListener = addDialogListener;
        createDialog(context, title, null);
    }

    public static void addingFirst(Context context, AddDialogListener addDialogListener) {
        String title = context.getString(R.string.dialog_title_add_first_serial);
        EditDialog.addDialogListener = addDialogListener;
        createDialog(context, title, null);
    }

    public static void editing(Context context, Serial oldSerial, EditDialogListener editDialogListener) {
        String title = context.getString(R.string.dialog_title_edit);
        EditDialog.editDialogListener = editDialogListener;
        createDialog(context, title, oldSerial);
    }

    private static void resetListeners() {
        editDialogListener = null;
        addDialogListener = null;
    }


    private static void createDialog(final Context context, String title, final Serial oldSerial) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater factory = LayoutInflater.from(context);
        View dialog_view = factory.inflate(R.layout.serial_dialog, null);

        final EditText serialNameET = (EditText) dialog_view.findViewById(R.id.serial_name_input);
        final EditText episodeET = (EditText) dialog_view.findViewById(R.id.episodeInput);
        final EditText seasonET = (EditText) dialog_view.findViewById(R.id.seasonInput);
        final EditText epsET = (EditText) dialog_view.findViewById(R.id.eppsInput);
        final EditText noteET = (EditText) dialog_view.findViewById(R.id.noteInput);

        if (oldSerial != null) {
            String oldSerialName = oldSerial.getName();
            int oldEpisode = oldSerial.getEpisode();
            int oldSeason = oldSerial.getSeason();
            int oldEpisodesPerSeason = oldSerial.getEps();
            String oldNote = oldSerial.getNote();

            serialNameET.setText(oldSerialName);
            serialNameET.setSelection(serialNameET.getText().length());

            episodeET.setText(String.valueOf(oldEpisode));
            seasonET.setText(String.valueOf(oldSeason));
            if (oldEpisodesPerSeason != 0) {
                epsET.setText(String.valueOf(oldEpisodesPerSeason));
            }
            noteET.setText(oldNote);
        }

        builder.setTitle(title)
                .setView(dialog_view)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.dialog_button_save), null)
                .setNeutralButton(context.getString(R.string.dialog_button_cancel_text), null);

        if (oldSerial != null && editDialogListener != null) {
            builder.setNegativeButton(context.getString(R.string.dialog_button_delete), null);
        }


        final AlertDialog alert = builder.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                Button cancelButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                        resetListeners();
                    }
                });

                if (oldSerial != null && editDialogListener != null) {
                    Button deleteButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SerialsActions.onEdit(context, oldSerial);
                            SerialsActions.onDelete(context, oldSerial);
                            JsonDatabase.deleteSerial(context, oldSerial);
                            List<Serial> serials = JsonDatabase.getSerials(context);
                            if (serials.size() > 0 && !Utils.firstStart) {
                                AdManager.showDeleteActionAd();
                            }
                            dialog.cancel();
                            Toast.makeText(context, oldSerial.getName() + " " + context.getString(R.string.toast_serial_removed),
                                    Toast.LENGTH_LONG).show();

                            editDialogListener.onDelete(context, oldSerial);
                            resetListeners();
                        }
                    });
                }

                Button saveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @SuppressWarnings("ConstantConditions")
                    @Override
                    public void onClick(View view) {
                        String newSerialName = String.valueOf(serialNameET.getText()).trim();
                        if (newSerialName != null && !newSerialName.isEmpty()) {
                            Serial serial = new Serial(newSerialName);
                            if (oldSerial== null && JsonDatabase.isSerialExist(context, serial)) {
                                Toast.makeText(context, context.getString(R.string.toast_serial_already_exist),
                                        Toast.LENGTH_SHORT).show();
                                serialNameET.requestFocus();
                            } else {
                                serial.setEpisode(stringToInt(episodeET.getText(), 1));
                                serial.setSeason(stringToInt(seasonET.getText(), 1));
                                serial.setEps(stringToInt(epsET.getText(), 0));
                                serial.setNote(noteET.getText().toString().trim());

                                if (oldSerial != null) {
                                    if (oldSerial.getEpisode() != serial.getEpisode() ||
                                            oldSerial.getSeason() != serial.getSeason()) {
                                        // Serial's episode/season changed
                                        SerialsActions.onEpisodeUpdate(context, serial);
                                    }

                                    if (!oldSerial.getName().equals(newSerialName)) {
                                        // Serial renamed
                                        Serial newSerial = new Serial(oldSerial);
                                        newSerial.rename(newSerialName);

                                        SerialsActions.onRename(context, oldSerial, newSerial);
                                        JsonDatabase.renameSerial(context, oldSerial, newSerial);
                                    }
                                } else {
                                    // Serial created
                                    SerialsActions.onCreate(context, serial);
                                }

                                SerialsActions.onEdit(context, serial);
                                JsonDatabase.saveSerial(context, serial);
                                alert.cancel();

                                if (editDialogListener == null) {
                                    addDialogListener.onSave(context, serial);
                                } else {
                                    editDialogListener.onSave(context, serial);
                                }
                                resetListeners();
                            }
                        } else {
                            Toast.makeText(context, context.getString(R.string.toast_empty_name),
                                    Toast.LENGTH_SHORT).show();
                            serialNameET.requestFocus();
                        }
                    }
                });
            }
        });


        alert.setCanceledOnTouchOutside(true);
        alert.show();


    }

    private static int stringToInt(Editable editable, int defaultValue) {
        try {
            return Integer.parseInt(String.valueOf(editable).trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }


}
