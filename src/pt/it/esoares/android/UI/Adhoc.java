package pt.it.esoares.android.ui;

import java.io.IOException;
import java.util.Locale;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import pt.it.esoares.android.devices.Network;
import pt.it.esoares.android.ip.IPGenerator;
import pt.it.esoares.android.ip.IPInfo;
import pt.it.esoares.android.ip.Utils;
import pt.it.esoares.android.olsrdeployer.R;

public class Adhoc extends ActionBarActivity implements ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the sections. We use a
	 * {@link FragmentPagerAdapter} derivative, which will keep every loaded fragment in memory. If this becomes too memory intensive, it
	 * may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	private static Network network;

	private IPInfo ipInfo;
	private String infoFragmentTAG;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adhoc);
		loadSettings();
		setupUI();
	}

	@Override
	protected void onResume() {
		loadSettings();
		super.onResume();
	}

	private void loadSettings() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		changeNetwork(Network.getFromPreferences(prefs));
		Utils.changeWifiState(this, true);
		prefs.edit().putString("mac_address", IPGenerator.getMacAddress()).commit();

		try {
			changeIP(IPInfo.getFromPreferences(prefs));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void changeNetwork(Network newNetwork) {
		network = newNetwork;
		if (infoFragmentTAG != null) {
			((InfoFragment) getSupportFragmentManager().findFragmentByTag(infoFragmentTAG)).setNetwork(network);
		}
	}

	public void changeIP(IPInfo newIP) {
		this.ipInfo = newIP;
		if (infoFragmentTAG != null) {
			((InfoFragment) getSupportFragmentManager().findFragmentByTag(infoFragmentTAG)).setIP(ipInfo);
		}
	}

	public Network getNetwork() {
		return network;
	}

	public IPInfo getIP() {
		return ipInfo;
	}

	public void setInfoFragmentTAG(String TAG) {
		this.infoFragmentTAG = TAG;
	}

	private void setupUI() {
		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.adhoc, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent i = new Intent(this, SettingsActivity.class);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class below).
			if (position == 0)
				return InfoFragment.newInstance(position);
			else
				return SearchNetworksFragment.newInstance(position);
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_info).toUpperCase(l);
			case 1:
				return getString(R.string.title_search_networks).toUpperCase(l);
			}
			return null;
		}
	}

}
