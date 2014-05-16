package pt.it.esoares.android.ui;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;

import pt.it.esoares.android.ip.IPGenerator;
import pt.it.esoares.android.ip.Utils;
import pt.it.esoares.android.olsr.TestOLSRExistence;
import pt.it.esoares.android.olsrdeployer.R;
import pt.it.esoares.android.util.FileCopyFromResources;
import pt.it.esoares.android.util.GenericExecutionCallback;
import pt.it.esoares.android.wpa_supplicant.TestWpaCliExistence;
import pt.it.esoares.android.wpa_supplicant.WpaCliDeployException;
import pt.it.esoares.android.wpa_supplicant.WpaCliDeployListener;
import pt.it.esoares.android.wpa_supplicant.WpaCliDeployer;

public class Setup extends ActionBarActivity {

	public final static String MAC_ADDRESS = "mac address";
	public final static String FILES_PATH = "files path";
	public final static String OLSR_PATH = "olsr path";
	public final static String OLSR_CONFIG_PATH = "olsr config path";
	public final static String WPA_CLI_PATH = "wpa_cli path";
	private final static String TAG = "SETUP";
	public static final String SETUP_DONE = "setup";

	private String olsrPath;
	private String olsrConfigPath;
	private String wpaCliPath;

	boolean setupOLSR = false;
	boolean setupWPACli = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
		startSetup();
	}

	private void startSetup() {
		String filesPath = getFilesDir().getAbsolutePath();
		olsrPath = filesPath + File.separatorChar + "olsrd";
		olsrConfigPath = filesPath + File.separatorChar + "olsr.conf";
		wpaCliPath = filesPath + File.separatorChar + "wpa_cli";

		Utils.changeWifiState(this, true);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = prefs.edit();

		editor.putString(MAC_ADDRESS, IPGenerator.getMacAddress());
		editor.putString(FILES_PATH, filesPath);
		editor.putString(OLSR_CONFIG_PATH, olsrConfigPath);
		editor.putString(OLSR_PATH, olsrPath);
		editor.putString(WPA_CLI_PATH, wpaCliPath);

		editor.commit();

		// OLSR
		setupOLSR();

		// wpa_cli
		setupWPACli();

	}

	private void setupOLSR() {
		new TestOLSRExistence().execute(olsrPath, new GenericExecutionCallback() {

			@Override
			public void onUnsucessfullExecution() {
				// deploy it
				new FileCopyFromResources().execute(getResources(), R.raw.olsrd, olsrPath,
						new GenericExecutionCallback() {

							@Override
							public void onUnsucessfullExecution() {
								Log.e(TAG, "ERROR setuping up OLSR");
								setupOLSR = true;
								updateUI();
							}

							@Override
							public void onSucessfullExecution() {
								Log.d(TAG, "sucessfull setting up OLSR");
								setupOLSR = true;
								updateUI();
							}
						});
			}

			@Override
			public void onSucessfullExecution() {
				Log.d(TAG, "sucessfull setting up OLSR");
				setupOLSR = true;
				updateUI();
			}

		});
	}

	private void setupWPACli() {
		new TestWpaCliExistence().execute(new String[] { wpaCliPath }, new GenericExecutionCallback() {

			@Override
			public void onUnsucessfullExecution() {
				setupWPACli = true;
				updateUI();
			}

			@Override
			public void onSucessfullExecution() {
				new WpaCliDeployer().execute(getResources(), R.raw.wpa_cli, wpaCliPath, new WpaCliDeployListener() {

					@Override
					public void onError(WpaCliDeployException e) {
						setupWPACli = true;
						Log.e(TAG, "Error on deploying wpa_cli: " + e.toString());
						updateUI();
					}

					@Override
					public void onDeployStatusUpdate(int percentage, int status) {
					}

					@Override
					public void onDeployFinish() {
						Log.d(TAG, "sucessfull deployed wpa_cli");
						setupWPACli = true;
						updateUI();
					}
				});
			}
		});
	}

	private void updateUI() {
		if (setupOLSR && setupWPACli) {
			// everything finished
			Toast.makeText(this, "Finish setup", Toast.LENGTH_LONG).show();
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			Editor editor = prefs.edit();
			editor.putBoolean(SETUP_DONE, true);
			editor.commit();
			Intent i = new Intent(this, Adhoc.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_setup, container, false);
			return rootView;
		}
	}

}
