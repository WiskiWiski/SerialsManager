package by.wiskiw.serialsmanager.settings.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import by.wiskiw.serialsmanager.R;
import by.wiskiw.serialsmanager.defaults.Constants;
import by.wiskiw.serialsmanager.settings.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = Constants.TAG + ":SettingsActv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupToolbar();
        setSettingsFragment(new SettingsFragment(), false, false);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null && getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private void setSettingsFragment(Fragment fragment, boolean addToStack, boolean showAnimation) {
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        if (showAnimation) {
            /*
            transaction.setCustomAnimations(R.animator.settings_enter, R.animator.settings_exit,
                    R.animator.settings_pop_enter, R.animator.settings_pop_exit);
                    */
        }
        transaction.replace(R.id.root_container, fragment);
        if (addToStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

}
