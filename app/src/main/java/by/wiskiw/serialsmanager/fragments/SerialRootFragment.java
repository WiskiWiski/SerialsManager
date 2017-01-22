package by.wiskiw.serialsmanager.fragments;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import by.wiskiw.serialsmanager.R;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by WiskiW on 27.12.2016.
 */

public class SerialRootFragment extends DialogFragment {

    ViewPager viewPager;
    CircleIndicator indicator;

    TextView cancelButton;
    TextView deleteButton;
    TextView saveButton;

    public SerialRootFragment() {

    }


    public static SerialRootFragment newInstance(String title) {
        SerialRootFragment frag = new SerialRootFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_root_fragment, container, false);

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        indicator = (CircleIndicator) rootView.findViewById(R.id.indicator);

        cancelButton = (TextView) rootView.findViewById(R.id.cancelButton);
        deleteButton = (TextView) rootView.findViewById(R.id.deleteButton);
        saveButton = (TextView) rootView.findViewById(R.id.saveButton);
        initButtons();


        setupViewPager(viewPager);
        indicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int item = viewPager.getCurrentItem()==0?1:0;
                viewPager.setCurrentItem(item);
            }
        });


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

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new EditTabFragment());
        adapter.addFragment(new InfoTabFragment());
        viewPager.setAdapter(adapter);
        indicator.setViewPager(viewPager);
        adapter.registerDataSetObserver(indicator.getDataSetObserver());
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
