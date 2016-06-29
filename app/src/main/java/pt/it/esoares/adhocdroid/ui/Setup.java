package pt.it.esoares.adhocdroid.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import pt.it.esoares.adhocdroid.ip.IPGenerator;
import pt.it.esoares.adhocdroid.ip.Utils;
import pt.it.esoares.adhocdroid.R;
import pt.it.esoares.adhocdroid.util.GenericExecutionCallback;
import pt.it.esoares.adhocdroid.util.ZipCopy;
import pt.it.esoares.adhocdroid.wpa_supplicant.TestWpaCliExistence;
import pt.it.esoares.adhocdroid.wpa_supplicant.WpaCliDeployException;
import pt.it.esoares.adhocdroid.wpa_supplicant.WpaCliDeployListener;
import pt.it.esoares.adhocdroid.wpa_supplicant.WpaCliDeployer;

public class Setup extends AppCompatActivity {

	public final static String MAC_ADDRESS = "mac address";
	public final static String FILES_PATH = "files customProtocolsPath";
	public final static String CUSTOM_PROTOCOLS_PATH = "olsr customProtocolsPath";
	public final static String SDCARD_PROTOCOLS_PATH = "olsr config customProtocolsPath";
	public final static String WPA_CLI_PATH = "wpa_cli customProtocolsPath";
	public static final String SETUP_DONE = "setup";
	private final static String TAG = "SETUP";
	boolean setupRoutingProtocols = false;
	boolean setupWPACli = false;
	private String customProtocolsPath;
	private String sdcardProtocolsPath;
	private String wpaCliPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup);

		startSetup();
	}

	private void startSetup() {
		String filesPath = getFilesDir().getAbsolutePath();
		customProtocolsPath = filesPath + File.separatorChar + "customProtocols";
		sdcardProtocolsPath = new File(Environment.getExternalStorageDirectory(), "routing_protocols").getAbsolutePath();// todo document this
		wpaCliPath = filesPath + File.separatorChar + "wpa_cli";

		Utils.changeWifiState(this, true);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = prefs.edit();

		editor.putString(MAC_ADDRESS, IPGenerator.getMacAddress());
		editor.putString(FILES_PATH, filesPath);
		editor.putString(SDCARD_PROTOCOLS_PATH, sdcardProtocolsPath);
		editor.putString(CUSTOM_PROTOCOLS_PATH, customProtocolsPath);
		editor.putString(WPA_CLI_PATH, wpaCliPath);

		editor.commit();

		// load custom routing protocols
		setupRoutingProtocols();

		// wpa_cli
		setupWPACli();

	}

	private void setupRoutingProtocols() {
		File pathOnSDCard = getApplicationContext().getExternalFilesDir(null);
		assert pathOnSDCard != null;
		new AsyncTask<String, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(String... params) {
				File sdcardProtocols = new File(params[1]);
				File memoryProtocols = new File(params[0]);
				File[] files = sdcardProtocols.listFiles();
				if (files == null) {
					sdcardProtocols.mkdirs();
					return false;
				}
				for (int i = 0; i < files.length; i++) {
					if (ZipCopy.isZip(files[i])) {
						File dest = new File(memoryProtocols, files[i].getName().substring(0, files[i].getName().length() - 4));
						dest.mkdirs();
						try {
							ZipCopy.copyFromZip(files[i], dest);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				return true;
			}

			@Override
			protected void onPostExecute(Boolean success) {
				setupRoutingProtocols = true;
				updateUI();
				Log.e(TAG, "ERROR at setup up Routing protocols");
				super.onPostExecute(success);
			}
		}.execute(customProtocolsPath, sdcardProtocolsPath);

	}

	private void setupWPACli() {
		new TestWpaCliExistence().execute(new String[]{wpaCliPath}, new GenericExecutionCallback() {

			@Override
			public void onUnsuccessfulExecution() {
				setupWPACli = true;
				updateUI();
			}

			@Override
			public void onSuccessfulExecution() {
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
						Log.d(TAG, "successful deployed wpa_cli");
						setupWPACli = true;
						updateUI();
					}
				});
			}
		});
	}

	private void updateUI() {
		if (setupRoutingProtocols && setupWPACli) {
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

}
