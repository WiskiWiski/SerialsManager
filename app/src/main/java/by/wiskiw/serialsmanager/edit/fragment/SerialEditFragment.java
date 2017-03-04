package by.wiskiw.serialsmanager.edit.fragment;

import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import by.wiskiw.serialsmanager.R;
import by.wiskiw.serialsmanager.edit.fragment.tabs.EditTabFragment;
import by.wiskiw.serialsmanager.edit.fragment.tabs.InfoTabFragment;
import by.wiskiw.serialsmanager.objects.Serial;

/**
 * Created by WiskiW on 27.12.2016.
 */

public class SerialEditFragment extends DialogFragment {

    private static final String BUNDLE_SERIAL = "serial_tag";

    private Serial serial;
    private  ViewPager viewPager;
    private  TextView cancelButton;
    private  TextView deleteButton;
    private  TextView saveButton;
    private EditFragmentListener editFragmentListener;

    public SerialEditFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey(BUNDLE_SERIAL)) {
            serial = getArguments().getParcelable(BUNDLE_SERIAL);
        } else {
            throw new IllegalArgumentException("Must be created through newInstance(...)");
        }
    }

    public static SerialEditFragment newInstance(Serial serial) {
        final SerialEditFragment fragment = new SerialEditFragment();
        final Bundle arguments = new Bundle();
        arguments.putParcelable(BUNDLE_SERIAL, serial);
        fragment.setArguments(arguments);
        return fragment;
    }

    public void setEditFragmentListener(EditFragmentListener editFragmentListener) {
        this.editFragmentListener = editFragmentListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_root_fragment, container, false);

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);

        cancelButton = (TextView) rootView.findViewById(R.id.cancelButton);
        deleteButton = (TextView) rootView.findViewById(R.id.deleteButton);
        saveButton = (TextView) rootView.findViewById(R.id.saveButton);
        initButtons();


        setupViewPager(viewPager);
        return rootView;
    }

    private void initButtons() {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editFragmentListener.onDelete(serial);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editFragmentListener.onSave(serial);
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new EditTabFragment());
        adapter.addFragment(new InfoTabFragment());
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }
    }
}
