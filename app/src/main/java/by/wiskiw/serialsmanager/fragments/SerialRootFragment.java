package by.wiskiw.serialsmanager.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import by.wiskiw.serialsmanager.R;
import by.wiskiw.serialsmanager.defaults.Constants;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by WiskiW on 27.12.2016.
 */

public class SerialRootFragment extends DialogFragment {

    ViewPager viewPager;
    CircleIndicator indicator;

    public SerialRootFragment() {

    }


    @Override
    public void onResume() {
        super.onResume();

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

        setupViewPager(viewPager);
        indicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Constants.TAG, "on:" + viewPager.getCurrentItem());
            }
        });



        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new EditTabFragment(), "Edit");
        adapter.addFragment(new InfoTabFragment(), "Info");
        viewPager.setAdapter(adapter);
        indicator.setViewPager(viewPager);
        adapter.registerDataSetObserver(indicator.getDataSetObserver());
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
