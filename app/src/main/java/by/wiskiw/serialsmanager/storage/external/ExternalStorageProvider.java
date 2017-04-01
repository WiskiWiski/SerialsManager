package by.wiskiw.serialsmanager.storage.external;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import by.wiskiw.serialsmanager.R;
import by.wiskiw.serialsmanager.app.Constants;
import by.wiskiw.serialsmanager.serial.Serial;
import by.wiskiw.serialsmanager.serial.notifications.SeAlarmManager;
import by.wiskiw.serialsmanager.settings.activities.SettingsActivity;
import by.wiskiw.serialsmanager.storage.PreferencesStorage;
import by.wiskiw.serialsmanager.storage.json.JsonDatabase;

/**
 * Created by WiskiW on 31.03.2017.
 */

public class ExternalStorageProvider {

    private static final String TAG = Constants.TAG + ":ExtStorProvider";

    private static final String DIR_PATH = "/TVSerials/";
    private static final String FILENAME = "backup%d.json";

    private static final int IMPORT_RESULT_CODE = 1515;

    public static void exportDatabase(Context context, String filename) {
        String string = PreferencesStorage.getJson(context).toString();
        FileOutputStream outputStream;

        try {
            new File(getFullPath()).mkdirs();

            outputStream = new FileOutputStream(getFullPath() + filename);
            outputStream.write(string.getBytes());
            outputStream.close();
            Toast.makeText(context,
                    String.format(context.getString(R.string.external_storage_export_msg_complete), DIR_PATH + filename),
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.w(TAG, "ExportDatabase: Can't export to the file ", e);
            Toast.makeText(context, context.getString(R.string.external_storage_export_msg_err), Toast.LENGTH_LONG).show();
        }
    }

    public static void importDatabase(Context context) {
        Intent intent = new Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT);
        ((Activity) context).startActivityForResult(Intent.createChooser(intent,
                context.getString(R.string.external_storage_import_title_select_file)), IMPORT_RESULT_CODE);
    }

    public static boolean onActivityResult(Context context, int requestCode, Intent data) {
        if (requestCode == IMPORT_RESULT_CODE) {
            if (data == null) return true;
            Uri selectedFileUri = data.getData(); //The uri with the location of the file
            if (selectedFileUri.getLastPathSegment().endsWith("json")) {

                String shortPath = '/' + selectedFileUri.getPath().split(":")[1]; // /TVSerials/backup_1.json
                String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + shortPath;

                try {
                    FileInputStream fis = new FileInputStream(new File(fullPath));
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader bufferedReader = new BufferedReader(isr);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }
                    fis.close();

                    JSONObject jsonObject = PreferencesStorage.parseJsonString(sb.toString());
                    List<Serial> serialList = JsonDatabase.parseJsonObject(jsonObject);
                    if (serialList != null && serialList.size() > 0) {
                        Toast.makeText(context,
                                String.format(context.getString(R.string.external_storage_import_msg_complete), shortPath),
                                Toast.LENGTH_LONG).show();
                        PreferencesStorage.saveJson(context, jsonObject);
                        for (Serial serial : serialList) {
                            SeAlarmManager.setAlarm(context, serial);
                        }
                        ((Activity) context).setResult(SettingsActivity.RESULT_LIST_UPDATED, new Intent());
                    } else {
                        Toast.makeText(context, context.getString(R.string.external_storage_import_msg_err_parse),
                                Toast.LENGTH_LONG).show();
                        Log.w(TAG, "Couldn't parse the json database!");
                    }
                } catch (IOException e) {
                    Toast.makeText(context, context.getString(R.string.external_storage_import_msg_err_read),
                            Toast.LENGTH_LONG).show();
                    Log.w(TAG, "Couldn't read the file!", e);
                }

            } else {
                Toast.makeText(context, context.getString(R.string.external_storage_import_msg_invalid_type),
                        Toast.LENGTH_LONG).show();
                Log.d(TAG, "Invalid file type");
                importDatabase(context);
            }
            return false;
        }
        return true;
    }


    public static String generateFileName() {
        String fileName = FILENAME.replace("%d", "");
        String filePath = Environment.getExternalStorageDirectory() + DIR_PATH + fileName;
        int i = 0;
        File file = new File(filePath);
        while (file.exists()) {
            i++;
            fileName = FILENAME.replace("%d", "_" + String.valueOf(i));
            filePath = Environment.getExternalStorageDirectory() + DIR_PATH + fileName;
            file = new File(filePath);
        }
        return fileName;
    }

    public static String getFullPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + getShortPath();
    }

    public static String getShortPath() {
        return DIR_PATH;
    }

}
