package by.wiskiw.serialsmanager.edit.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import by.wiskiw.serialsmanager.R;
import by.wiskiw.serialsmanager.defaults.Constants;
import by.wiskiw.serialsmanager.edit.fragment.tabs.EditTabFragment;
import by.wiskiw.serialsmanager.edit.fragment.tabs.InfoTabFragment;
import by.wiskiw.serialsmanager.objects.Serial;
import by.wiskiw.serialsmanager.storage.json.JsonDatabase;

/**
 * Created by WiskiW on 27.12.2016.
 */

public class SerialEditFragment extends DialogFragment {

    private static final String TAG = Constants.TAG + ":EditFragment";

    private static final String BUNDLE_SERIAL = "serial_tag";
    private static final String BUNDLE_TITLE = "title_tag";

    private Serial serial;
    private String dialogTitle;

    private TextView cancelButton;
    private TextView deleteButton;
    private TextView saveButton;

    private EditTabFragment editTabFragment;
    private InfoTabFragment infoTabFragment;

    private OnCreateListener onCreateListener;
    private OnEditListener onEditListener;
    private OnDeleteListener onDeleteListener;

    public SerialEditFragment() {
    }

    public static SerialEditFragment show(FragmentManager fm, String title, Serial serial) {
        SerialEditFragment serialEditFragment = SerialEditFragment.newInstance(title, serial);
        serialEditFragment.show(fm, "fragment_edit_name");
        return serialEditFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey(BUNDLE_SERIAL) && args.containsKey(BUNDLE_TITLE)) {
            serial = args.getParcelable(BUNDLE_SERIAL);
            dialogTitle = args.getString(BUNDLE_TITLE);
        } else {
            throw new IllegalArgumentException("Must be created through newInstance(...)");
        }
    }

    public static SerialEditFragment newInstance(String title, Serial serial) {
        final SerialEditFragment fragment = new SerialEditFragment();
        final Bundle arguments = new Bundle();
        arguments.putParcelable(BUNDLE_SERIAL, serial);
        arguments.putString(BUNDLE_TITLE, title);
        fragment.setArguments(arguments);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_serial_edit, container, false);

        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        editTabFragment = EditTabFragment.newInstance(serial);
        infoTabFragment = InfoTabFragment.newInstance(serial);

        TextView dialogTitleTextView = (TextView) rootView.findViewById(R.id.dialog_title);
        dialogTitleTextView.setText(dialogTitle);

        cancelButton = (TextView) rootView.findViewById(R.id.cancelButton);
        deleteButton = (TextView) rootView.findViewById(R.id.deleteButton);
        saveButton = (TextView) rootView.findViewById(R.id.saveButton);
        setupButtonsConstructors();

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        return rootView;
    }


    private void setupButtonsConstructors() {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        if (serial != null) {
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onDeleteListener != null) {
                        onDeleteListener.onDelete(serial);
                    }
                    getDialog().dismiss();
                }
            });
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Serial newSerial = getUpdatedSerial();
                Context context = getContext();
                if (newSerial != null && !newSerial.getName().isEmpty()) {
                    if (serial == null && JsonDatabase.isSerialExist(context, newSerial)) {
                        Toast.makeText(context, getString(R.string.toast_serial_already_exist),
                                Toast.LENGTH_SHORT).show();
                        setFocusToNameEditText();
                    } else {
                        if (serial != null) {
                            // Serial edit
                            if (onEditListener != null) {
                                onEditListener.onEdit(serial, newSerial);
                            }
                        } else {
                            // Serial created
                            if (onCreateListener != null) {
                                onCreateListener.onCreate(newSerial);
                            }
                        }
                        getDialog().dismiss();
                    }
                } else {
                    Toast.makeText(context, getString(R.string.toast_empty_name),
                            Toast.LENGTH_SHORT).show();
                    setFocusToNameEditText();
                }

            }
        });
    }

    private void setFocusToNameEditText() {
        if (editTabFragment != null)
            editTabFragment.serialNameET.requestFocus();
    }

    private Serial getUpdatedSerial() {
        if (editTabFragment != null && infoTabFragment != null) {
            Serial serial = editTabFragment.getSerialData();
            serial.enableNotifications(infoTabFragment.getNotificationSwitcher());
            return serial;
        } else {
            return serial;
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(editTabFragment);
        adapter.addFragment(infoTabFragment);
        viewPager.setAdapter(adapter);
    }

    public void setOnCreateListener(OnCreateListener onCreateListener) {
        this.onCreateListener = onCreateListener;
    }

    public void setOnEditListener(OnEditListener onEditListener) {
        this.onEditListener = onEditListener;
    }

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    public interface OnCreateListener {
        void onCreate(Serial serial);
    }

    public interface OnEditListener {
        void onEdit(Serial oldSerial, Serial newSerial);
    }

    public interface OnDeleteListener {
        void onDelete(Serial serial);
    }


}
