package by.wiskiw.serialsmanager.edit.dialog;

import android.content.Context;

import by.wiskiw.serialsmanager.objects.Serial;

/**
 * Created by WiskiW on 25.12.2016.
 */

public interface AddDialogListener {
    void onSave(Context context, Serial serial);
}
