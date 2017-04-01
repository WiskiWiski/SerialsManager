package by.wiskiw.serialsmanager.main.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import by.wiskiw.serialsmanager.R;
import by.wiskiw.serialsmanager.app.App;
import by.wiskiw.serialsmanager.app.Constants;
import by.wiskiw.serialsmanager.main.fragments.MainFragment;
import by.wiskiw.serialsmanager.managers.AdManager;
import by.wiskiw.serialsmanager.rate.RateDialog;
import by.wiskiw.serialsmanager.settings.activities.SettingsActivity;
import by.wiskiw.serialsmanager.storage.PreferencesStorage;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = Constants.TAG + ":MainActv";

    public static final int REQUEST_CODE_SETTINGS_PERMISSIONS = 3200;

    protected MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MainFragment.F_TAG);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (App.getLaunchNumb() < 3) {
            Menu navMenu = navigationView.getMenu();
            navMenu.findItem(R.id.nav_rate_app).setVisible(false);
        }


        PreferencesStorage.syncWithRemoteConfig(this);
        AdManager.prepareAd(this);

        setMainFragment();
    }

    private void setMainFragment() {
        if (mainFragment == null)
            mainFragment = new MainFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction
                .replace(R.id.root_container, mainFragment, MainFragment.F_TAG)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Toolbar menu here
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_plus:
                if (mainFragment != null) {
                    mainFragment.addNewSerial();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Navigation Drawer menu here
        int id = item.getItemId();

        if (id == R.id.nav_serials) {
            setMainFragment();
        } else if (id == R.id.nav_settings) {
            startSettingsActivity();
        } else if (id == R.id.nav_rate_app) {
            RateDialog.createDialog(this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startSettingsActivity() {
        if (isStorageAllow()) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivityForResult(settingsIntent, SettingsActivity.REQUEST_CODE);
        }
    }

    private boolean isStorageAllow() {
        // TODO : https://developer.android.com/training/permissions/requesting.html
        String permissionList[] = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        int nGranted = 0;
        for (String aPermissionList : permissionList) {
            if (ContextCompat.checkSelfPermission(this, aPermissionList) == PackageManager.PERMISSION_GRANTED) {
                nGranted++;
            }
        }
        if (nGranted != permissionList.length) {
            Log.d(TAG, "Request for storage permissions.");
            ActivityCompat.requestPermissions(this, permissionList, REQUEST_CODE_SETTINGS_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SettingsActivity.REQUEST_CODE) {
            if (resultCode == SettingsActivity.RESULT_LIST_UPDATED) {
                mainFragment = null;
                setMainFragment();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == REQUEST_CODE_SETTINGS_PERMISSIONS) {
                startSettingsActivity();
            }
        } else {
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.root_container), getString(R.string.pref_msg_permissions_not_granted),
                            Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
