package by.wiskiw.serialsmanager.main.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import by.wiskiw.serialsmanager.app.App;
import by.wiskiw.serialsmanager.defaults.Constants;
import by.wiskiw.serialsmanager.managers.AdManager;
import by.wiskiw.serialsmanager.R;
import by.wiskiw.serialsmanager.main.fragments.MainFragment;
import by.wiskiw.serialsmanager.notifications.BootService;
import by.wiskiw.serialsmanager.rate.RateDialog;
import by.wiskiw.serialsmanager.settings.activities.SettingsActivity;
import by.wiskiw.serialsmanager.storage.PreferencesStorage;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = Constants.TAG + ":MainActv";

    protected MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        mainFragment = new MainFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction
                .replace(R.id.root_container, mainFragment)
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
                Intent i = new Intent(this, BootService.class);
                startService(i);
                if (mainFragment != null) {
                    //mainFragment.addNewSerial();
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
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        } else if (id == R.id.nav_rate_app) {
            RateDialog.createDialog(this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
