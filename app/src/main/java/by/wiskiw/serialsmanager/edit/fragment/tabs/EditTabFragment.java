package by.wiskiw.serialsmanager.edit.fragment.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import by.wiskiw.serialsmanager.R;
import by.wiskiw.serialsmanager.defaults.Constants;
import by.wiskiw.serialsmanager.objects.Serial;

/**
 * Created by WiskiW on 28.12.2016.
 */

public class EditTabFragment extends Fragment {

    private static final String TAG = Constants.TAG + ":EditTab";

    private static final String BUNDLE_SERIAL = "serial_tag";

    private Serial oldSerial;

    public EditText serialNameET;
    private EditText episodeET;
    private EditText seasonET;
    private EditText epsET;
    private EditText noteET;

    public EditTabFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey(BUNDLE_SERIAL)) {
            oldSerial = args.getParcelable(BUNDLE_SERIAL);
        } else {
            throw new IllegalArgumentException("Must be created through newInstance(...)");
        }
    }

    public static EditTabFragment newInstance(Serial serial) {
        final EditTabFragment fragment = new EditTabFragment();
        final Bundle arguments = new Bundle();
        arguments.putParcelable(BUNDLE_SERIAL, serial);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_edit_tab, container, false);
        initViews(rootView);

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


        return rootView;
    }

    private void initViews(View rootView) {
        serialNameET = (EditText) rootView.findViewById(R.id.serial_name_input);
        serialNameET.requestFocus();
        episodeET = (EditText) rootView.findViewById(R.id.episodeInput);
        seasonET = (EditText) rootView.findViewById(R.id.seasonInput);
        epsET = (EditText) rootView.findViewById(R.id.eppsInput);
        noteET = (EditText) rootView.findViewById(R.id.noteInput);
    }

    public Serial getSerialData() {
        String newSerialName = String.valueOf(serialNameET.getText()).trim();
        Serial newSerial = new Serial(newSerialName);
        newSerial.setEpisode(stringToInt(episodeET.getText(), 1));
        newSerial.setSeason(stringToInt(seasonET.getText(), 1));
        newSerial.setEps(stringToInt(epsET.getText(), 0));
        newSerial.setNote(noteET.getText().toString().trim());

        return newSerial;
    }

    private static int stringToInt(Editable editable, int defaultValue) {
        try {
            return Integer.parseInt(String.valueOf(editable).trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

}
