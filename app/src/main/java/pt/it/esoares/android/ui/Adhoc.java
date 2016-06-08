package pt.it.esoares.android.ui;

import java.io.IOException;
import java.util.Locale;

import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import pt.it.esoares.android.devices.Device;
import pt.it.esoares.android.devices.DeviceFactory;
import pt.it.esoares.android.devices.Network;
import pt.it.esoares.android.ip.IPInfo;
import pt.it.esoares.android.ip.Utils;
import pt.it.esoares.android.olsr.OLSRKiller;
import pt.it.esoares.android.olsr.OLSRSetting;
import pt.it.esoares.android.olsrdeployer.R;
import pt.it.esoares.android.util.GenericExecutionCallback;
import pt.it.esoares.android.util.tasks.StartNetwork;
import pt.it.esoares.android.util.tasks.StartOLSR;
import pt.it.esoares.android.util.tasks.StopAdHocNetwork;

public class Adhoc extends AppCompatActivity implements ActionBar.TabListener {

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
	boolean useOLSR;
	private IPInfo ipInfo;
	private Device device;

	private String infoFragmentTAG;

	public static final String STATE_OLSR = "state olsr connected";
	public static final String STATE_CONNECTED = "state network connected";
	public static final String STATE_CONNECTING = "state network connecting";
	public static final String USE_OLSR = "use olsr";

	static final String DEVICE = "device";

	boolean connected = false;
	boolean connecting = false;
	boolean olsr_connected = false;

	private Menu menu;
	private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adhoc);
		loadSettings();
		setupUI();
		restoreState(savedInstanceState);
	}

	private void restoreState(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			return;
		} else {
			device = DeviceFactory.getDevice(this, savedInstanceState.getString(DEVICE));
			connected = savedInstanceState.getBoolean(STATE_CONNECTED, false);
			connecting = savedInstanceState.getBoolean(STATE_CONNECTING, false);
			if (infoFragmentTAG != null) {
				((InfoFragment) getSupportFragmentManager().findFragmentByTag(infoFragmentTAG)).changeConnectingState(
						connecting, connected);

			}
		}
	}

	@Override
	protected void onResume() {
		loadSettings();
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		saveStartState();
		outState.putBoolean(STATE_CONNECTED, connected);
		outState.putBoolean(STATE_CONNECTING, connecting);
		outState.putString(DEVICE, device == null ? null : device.getClassUniqIdentifier());
		super.onSaveInstanceState(outState);
	}

	private void saveStartState() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = prefs.edit();
		editor.putBoolean(STATE_CONNECTED, connected);
		editor.putBoolean(STATE_CONNECTING, connecting);
		editor.putBoolean(STATE_OLSR, olsr_connected);
		editor.commit();
	}

	private void loadSettings() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (!prefs.getBoolean(Setup.SETUP_DONE, false)) {
			Intent i = new Intent(this, Setup.class);
			startActivity(i);
		}
		changeNetwork(Network.getFromPreferences(prefs));
		try {
			changeIPInformation(IPInfo.getFromPreferences(prefs));
		} catch (IOException e) {
			e.printStackTrace();
		}
		useOLSR = prefs.getBoolean("use_olsr", true);
		connected = prefs.getBoolean(STATE_CONNECTED, false);
		olsr_connected = prefs.getBoolean(STATE_OLSR, false);
		// connecting = prefs.getBoolean(STATE_CONNECTING, false);
		connecting = false;
		if (infoFragmentTAG != null) {
			InfoFragment fragment = ((InfoFragment) getSupportFragmentManager().findFragmentByTag(infoFragmentTAG));
			if (fragment != null) {
				fragment.changeConnectingState(connecting, connected);
			}

		}
		prefListener=new SharedPreferences.OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
				if (key.equals(STATE_CONNECTED) || key.equals(STATE_CONNECTING)) {
					connected = sharedPreferences.getBoolean(STATE_CONNECTED, false);
					connecting = sharedPreferences.getBoolean(STATE_CONNECTING, false);
					if (infoFragmentTAG != null) {
						InfoFragment fragment = ((InfoFragment) getSupportFragmentManager().findFragmentByTag(infoFragmentTAG));
						if (fragment != null) {
							fragment.changeConnectingState(connecting, connected);
						}

					}
				}
			}
		};
		prefs.registerOnSharedPreferenceChangeListener(prefListener);
	}

	public void changeNetwork(Network newNetwork) {
		network = newNetwork;
		if (infoFragmentTAG != null) {
			((InfoFragment) getSupportFragmentManager().findFragmentByTag(infoFragmentTAG)).setNetwork(network);
		}
	}

	public void changeIPInformation(IPInfo newIP) {
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

	public void onStartStop(View v) {
		connecting = true;
		InfoFragment fragment = ((InfoFragment) getSupportFragmentManager().findFragmentByTag(infoFragmentTAG));
		if (fragment != null) {
			fragment.changeConnectingState(connecting, connected);
		}
		saveStartState();
		if (!connected) {
			// connects
			new StartNetwork(this.getApplicationContext()).execute(new GenericExecutionCallback() {

				@Override
				public void onUnsuccessfulExecution() {
					connecting = false;
					connected = false;
					InfoFragment fragment = ((InfoFragment) getSupportFragmentManager().findFragmentByTag(
							infoFragmentTAG));
					if (fragment != null) {
						fragment.changeConnectingState(connecting, connected);
					}
					Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
					saveStartState();
				}

				@Override
				public void onSuccessfulExecution() {
					connecting = false;
					connected = true;
					InfoFragment fragment = ((InfoFragment) getSupportFragmentManager().findFragmentByTag(
							infoFragmentTAG));
					if (fragment != null) {
						fragment.changeConnectingState(connecting, connected);
					}
					saveStartState();
				}

			}, this);
		} else {
			// disconnect
			new StopAdHocNetwork(device, useOLSR, new GenericExecutionCallback() {

				@Override
				public void onUnsuccessfulExecution() {
					Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onSuccessfulExecution() {
					connecting = false;
					connected = false;
					InfoFragment fragment = ((InfoFragment) getSupportFragmentManager().findFragmentByTag(
							infoFragmentTAG));
					if (fragment != null) {
						fragment.changeConnectingState(connecting, connected);
					}
					saveStartState();
					Utils.changeWifiState(getApplicationContext(), true);
				}
			}, this).stopNetwork();
		}
	}

	private void startStopOLSR() {
		if (!olsr_connected) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			String olsrConfigPath = prefs.getString(Setup.OLSR_CONFIG_PATH, null);
			String olsrPath = prefs.getString(Setup.OLSR_PATH, null);
			new StartOLSR(olsrConfigPath, olsrPath, new GenericExecutionCallback() {

				@Override
				public void onUnsuccessfulExecution() {
					Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onSuccessfulExecution() {
					olsr_connected = true;
					updateMenu(true);
				}
			}).startOlsr(this, new OLSRSetting());
		} else {
			new OLSRKiller().execute(new GenericExecutionCallback() {

				@Override
				public void onUnsuccessfulExecution() {
					Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onSuccessfulExecution() {
					olsr_connected = false;
					updateMenu(olsr_connected);
				}
			});
		}
	}

	protected void updateMenu(boolean olsr_connected) {
		// MenuItem startStopMenuItem = menu.findItem(R.id.startOLSR);
		// startStopMenuItem.setTitle(olsr_connected ? R.string.menu_stopOLSR : R.string.menu_startStopOLSR);
		// save setting
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = prefs.edit();
		editor.putBoolean(STATE_OLSR, olsr_connected);
		editor.commit();
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
		this.menu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent i;
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
				i = new Intent(this, DeprecatedSettingsActivity.class);
			} else {
				i = new Intent(this, SettingsActivity.class);
			}
			startActivity(i);
			return true;
		}
		// else if (id == R.id.startOLSR) {
		// startStopOLSR();
		// return true;
		// }
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
