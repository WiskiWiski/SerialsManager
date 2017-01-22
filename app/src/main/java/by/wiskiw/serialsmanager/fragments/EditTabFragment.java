package by.wiskiw.serialsmanager.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import by.wiskiw.serialsmanager.R;

/**
 * Created by WiskiW on 28.12.2016.
 */

public class EditTabFragment extends Fragment {

    EditText serialNameET;
    EditText episodeET;
    EditText seasonET;
    EditText epsET;
    EditText noteET;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_edit_tab,  container, false);
        serialNameET = (EditText) rootView.findViewById(R.id.serial_name_input);
        episodeET = (EditText) rootView.findViewById(R.id.episodeInput);
        seasonET = (EditText) rootView.findViewById(R.id.seasonInput);
        epsET = (EditText) rootView.findViewById(R.id.eppsInput);
        noteET = (EditText) rootView.findViewById(R.id.noteInput);
        return rootView;
    }

}
