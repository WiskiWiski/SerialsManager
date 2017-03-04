package by.wiskiw.serialsmanager.edit.fragment.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import by.wiskiw.serialsmanager.R;
import by.wiskiw.serialsmanager.objects.Serial;

/**
 * Created by WiskiW on 28.12.2016.
 */

public class InfoTabFragment extends Fragment {

    private static final String BUNDLE_SERIAL = "serial_tag";

    private Serial serial;

    public InfoTabFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey(BUNDLE_SERIAL)) {
            serial = args.getParcelable(BUNDLE_SERIAL);
        } else {
            throw new IllegalArgumentException("Must be created through newInstance(...)");
        }
    }

    public static InfoTabFragment newInstance(Serial serial) {
        final InfoTabFragment fragment = new InfoTabFragment();
        final Bundle arguments = new Bundle();
        arguments.putParcelable(BUNDLE_SERIAL, serial);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_info_tab, container, false);


        return rootView;
    }

    public boolean getNotifSwitcher(){
        return true; // TODO: Complete switcher
    }

}
