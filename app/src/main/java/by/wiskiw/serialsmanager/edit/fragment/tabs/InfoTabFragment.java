package by.wiskiw.serialsmanager.edit.fragment.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import by.wiskiw.serialsmanager.R;

/**
 * Created by WiskiW on 28.12.2016.
 */

public class InfoTabFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_info_tab,  container, false);
        return rootView;
    }
}
