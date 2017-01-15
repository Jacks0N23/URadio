package com.jassdev.apps.andrroider.uradio;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;

import com.jassdev.apps.andrroider.uradio.Chat.ChatFragment;
import com.jassdev.apps.andrroider.uradio.NowOnSite.NowOnSiteFragment;
import com.jassdev.apps.andrroider.uradio.Playlist.PlaylistFragment;
import com.jassdev.apps.andrroider.uradio.Radio.RadioFragment;
import com.jassdev.apps.andrroider.uradio.Settings.SettingsFragment;
import com.jassdev.apps.andrroider.uradio.Utils.Utils;
import com.jassdev.apps.andrroider.uradio.databinding.AnotherMainBinding;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "MainActivity";

    private AnotherMainBinding binding;

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private String mTitle = "Главная";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.another_main);
        toolbarSetup();
        navigationDrawerSetup();
        getFragmentManager().beginTransaction().replace(R.id.container_main, new RadioFragment()).commit();
    }

    private void toolbarSetup() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(mTitle);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white);
        }
        mTitle = getString(R.string.menu_radio);
    }


    private void navigationDrawerSetup() {
        actionBarDrawerToggle =
                new ActionBarDrawerToggle(this, binding.navDrawer, binding.toolbar, R.string.drawer_open,
                        R.string.drawer_close);
        binding.navDrawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        binding.navView.setNavigationItemSelectedListener(this);
        binding.navViewBottom.setNavigationItemSelectedListener(this);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        assert getSupportActionBar() != null;

        switch (menuItem.getItemId()) {
            case R.id.chat:
                fragment = new ChatFragment();
                getSupportActionBar().setTitle(getString(R.string.menu_chat));
                mTitle = getString(R.string.menu_chat);
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this);
                break;
            case R.id.now_on_site:
                fragment = new NowOnSiteFragment();
                getSupportActionBar().setTitle(getString(R.string.menu_now_on_site));
                mTitle = getString(R.string.menu_now_on_site);
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this);
                break;
            case R.id.radio:
                fragment = new RadioFragment();
                getSupportActionBar().setTitle(getString(R.string.menu_radio));
                mTitle = getString(R.string.menu_radio);
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this);
                break;

            case R.id.playlist:
                fragment = new PlaylistFragment();
                getSupportActionBar().setTitle(getString(R.string.menu_playlist));
                mTitle = getString(R.string.menu_playlist);
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this);
                break;
            case R.id.settings:
                fragment = new SettingsFragment();
                getSupportActionBar().setTitle(getString(R.string.menu_settings));
                mTitle = getString(R.string.menu_settings);
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this);
                break;
        }

        if (fragment != null) {
            fragmentTransaction.setCustomAnimations(R.animator.frag_in, R.animator.frag_out);
            fragmentTransaction.addToBackStack(fragment.getTag());
            fragmentTransaction.replace(R.id.container_main, fragment).commit();
        }

        binding.navDrawer.closeDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        restoreActionBar();
        return super.onCreateOptionsMenu(menu);
    }

    public void restoreActionBar() {
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setTitle(mTitle);
    }

    @Override
    protected void onDestroy() {
        binding.navDrawer.removeDrawerListener(actionBarDrawerToggle);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(mTitle);
        if (binding.navDrawer.isDrawerOpen(GravityCompat.START)) {
            binding.navDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
