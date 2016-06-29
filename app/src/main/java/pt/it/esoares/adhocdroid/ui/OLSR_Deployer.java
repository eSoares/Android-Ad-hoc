package pt.it.esoares.adhocdroid.ui;

import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import pt.it.esoares.adhocdroid.devices.Device;
import pt.it.esoares.adhocdroid.devices.DeviceFactory;
import pt.it.esoares.adhocdroid.devices.Network;
import pt.it.esoares.adhocdroid.ip.IPGenerator;
import pt.it.esoares.adhocdroid.R;
import pt.it.esoares.adhocdroid.util.tasks.FileRemover;
import pt.it.esoares.adhocdroid.util.GenericExecutionCallback;
import pt.it.esoares.adhocdroid.wpa_supplicant.GenerateWPA_supplicant;
import pt.it.esoares.adhocdroid.wpa_supplicant.ScanExistingNetworks;
import pt.it.esoares.adhocdroid.wpa_supplicant.ScanNetworkListener;
import pt.it.esoares.adhocdroid.wpa_supplicant.ScanNetworksException;
import pt.it.esoares.adhocdroid.wpa_supplicant.TestWpaCliExistence;
import pt.it.esoares.adhocdroid.wpa_supplicant.WpaCliDeployException;
import pt.it.esoares.adhocdroid.wpa_supplicant.WpaCliDeployListener;
import pt.it.esoares.adhocdroid.wpa_supplicant.WpaCliDeployer;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class OLSR_Deployer extends AppCompatActivity {
	static TextView status;
	ProgressDialog dialog;
	private String WPACLI_PATH;
	private Device device;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_olsr__deployer);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
		dialog = new ProgressDialog(this);
		WPACLI_PATH = getFilesDir().getAbsolutePath() + File.separatorChar + "wpa_cli";
		// status= (TextView) findViewById(R.id.txt_status);
		loadDevice();
	}

	private void loadDevice() {
		new AsyncTask<Context, Void, Device>() {

			@Override
			protected Device doInBackground(Context... params) {
				return DeviceFactory.getDevice(params[0]);
			}

			@Override
			protected void onPostExecute(Device result) {
				device = result;
				super.onPostExecute(result);
			}

		}.execute(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.olsr__deployer, menu);
		return true;
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
			View rootView = inflater.inflate(R.layout.fragment_olsr__deployer, container, false);
			status = (TextView) rootView.findViewById(R.id.txt_status);
			return rootView;
		}
	}

	public void wpa_supplicant(View v) {
		dialog.show();
		if (device == null) {
			// TODO
		}
		try {
			new GenerateWPA_supplicant().execute(device, new Network("Ad-Hoc"), new GenericExecutionCallback() {

				@Override
				public void onUnsuccessfulExecution() {
					setStatus("Failed to update wpa_supplicant", false);
					dialog.dismiss();
				}

				@Override
				public void onSuccessfulExecution() {
					setStatus("Success to update wpa_supplicant", true);
					dialog.dismiss();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void existsWPACli(View v) {
		new TestWpaCliExistence().execute(new String[] { WPACLI_PATH }, new GenericExecutionCallback() {

			@Override
			public void onUnsuccessfulExecution() {
				dialog.dismiss();
				setStatus("Missing wpa_cli", false);
			}

			@Override
			public void onSuccessfulExecution() {
				dialog.dismiss();
				setStatus("Found wpa_cli", true);
			}
		});
		dialog.show();
	}

	public void generateIPAddress(View v) {
		String result = IPGenerator.getMacAddress();
		try {
			InetAddress ip = IPGenerator.generateIP(result);
			setStatus(ip != null ? ip.getHostAddress() : "", ip != null ? true : false);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void scanExistingNetworks(View v) {
		new ScanExistingNetworks(new ScanNetworkListener() {

			@Override
			public void onNetworkFound(Network network) {
				Toast.makeText(getApplicationContext(), network.toString(), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onEndedWithError(ScanNetworksException scanNetworksException) {
				setStatus("Failed scan: " + scanNetworksException.getMessage(), false);
			}

			@Override
			public void onEnd() {
				setStatus("Successful Scan", true);
			}
		}).run(WPACLI_PATH);
	}

	public void deployWPACli(View v) {
		new WpaCliDeployer().execute(getResources(), R.raw.wpa_cli, WPACLI_PATH, new WpaCliDeployListener() {

			@Override
			public void onError(WpaCliDeployException e) {
				dialog.dismiss();
				setStatus("Failed to deploy wpa_cli", false);
			}

			@Override
			public void onDeployStatusUpdate(int percentage, int status) {
			}

			@Override
			public void onDeployFinish() {
				dialog.dismiss();
				setStatus("Successful deployed wpa_cli", true);

			}
		});
		dialog.show();
	}

	public void removeWPACli(View v) {
		dialog.show();
		new FileRemover().execute(new String[] { WPACLI_PATH }, new GenericExecutionCallback() {

			@Override
			public void onUnsuccessfulExecution() {
				dialog.dismiss();
				setStatus("Failed to remove file", false);
			}

			@Override
			public void onSuccessfulExecution() {
				dialog.dismiss();
				setStatus("Success to remove file", true);
			}
		});
	}

	private void setStatus(String value, boolean successful) {
		status.setText(value);
		status.setBackgroundResource(successful ? R.color.okey : R.color.error);
	}

	private void setStatus(int resourceStringID, boolean successful) {
		status.setText(resourceStringID);
		status.setBackgroundResource(successful ? R.color.okey : R.color.error);
	}

}
