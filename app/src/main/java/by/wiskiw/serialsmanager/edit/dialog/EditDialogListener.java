package by.wiskiw.serialsmanager.edit.dialog;

import android.content.Context;

import by.wiskiw.serialsmanager.objects.Serial;

public interface EditDialogListener extends AddDialogListener {
    void onDelete(Context context, Serial serial);
}