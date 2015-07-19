package pt.it.esoares.android.ui;

import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import pt.it.esoares.android.devices.Device;
import pt.it.esoares.android.devices.DeviceFactory;
import pt.it.esoares.android.devices.Network;
import pt.it.esoares.android.ip.IPGenerator;
import pt.it.esoares.android.olsr.ExecuteOLSR;
import pt.it.esoares.android.olsr.OLSRConfigDeploy;
import pt.it.esoares.android.olsr.OLSRGenerator;
import pt.it.esoares.android.olsr.OLSRRunningTest;
import pt.it.esoares.android.olsr.OLSRSetting;
import pt.it.esoares.android.olsr.TestOLSRExistence;
import pt.it.esoares.android.olsrdeployer.R;
import pt.it.esoares.android.util.tasks.FileCopyFromResources;
import pt.it.esoares.android.util.tasks.FileRemover;
import pt.it.esoares.android.util.GenericExecutionCallback;
import pt.it.esoares.android.wpa_supplicant.GenerateWPA_supplicant;
import pt.it.esoares.android.wpa_supplicant.ScanExistingNetworks;
import pt.it.esoares.android.wpa_supplicant.ScanNetworkListener;
import pt.it.esoares.android.wpa_supplicant.ScanNetworksException;
import pt.it.esoares.android.wpa_supplicant.TestWpaCliExistence;
import pt.it.esoares.android.wpa_supplicant.WpaCliDeployException;
import pt.it.esoares.android.wpa_supplicant.WpaCliDeployListener;
import pt.it.esoares.android.wpa_supplicant.WpaCliDeployer;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class OLSR_Deployer extends AppCompatActivity {
	Boolean existsOLSR;
	static TextView status;
	ProgressDialog dialog;
	private String OLSRD_PATH;
	private String OLSRD_CONFIG_PATH;
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
		OLSRD_PATH = getFilesDir().getAbsolutePath() + File.separatorChar + "olsrd";
		OLSRD_CONFIG_PATH = getFilesDir().getAbsolutePath() + File.separatorChar + "olsr.conf";
		WPACLI_PATH = getFilesDir().getAbsolutePath() + File.separatorChar + "wpa_cli";
		Log.d("OLSR DEPLOYER", "Generated path: " + OLSRD_PATH);
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

	public void existsOLSR(View v) {
		dialog.setTitle("Loading");
		dialog.show();
		new TestOLSRExistence().execute(OLSRD_PATH, new GenericExecutionCallback() {

			@Override
			public void onUnsucessfullExecution() {
				dialog.dismiss();
				setStatus(R.string.olsr_not_found, false);
			}

			@Override
			public void onSucessfullExecution() {
				dialog.dismiss();
				setStatus(R.string.olsr_found, true);
			}
		});
	}

	public void deployOLSR(View v) {
		new FileCopyFromResources().execute(getResources(), R.raw.olsrd, OLSRD_PATH, new GenericExecutionCallback() {

			@Override
			public void onUnsucessfullExecution() {
				dialog.dismiss();
				setStatus("Failed placing OLSR", false);
			}

			@Override
			public void onSucessfullExecution() {
				dialog.dismiss();
				setStatus("Sucess placing OLSR", true);
			}
		});
		dialog.show();
	}

	public void removeOLSR(View v) {
		new FileRemover().execute(new String[] { OLSRD_PATH }, new GenericExecutionCallback() {

			@Override
			public void onUnsucessfullExecution() {
				setStatus("Failed to remove file", false);
				dialog.dismiss();
			}

			@Override
			public void onSucessfullExecution() {
				setStatus("Sucess to remove file", true);
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void startOLSR(View v) {
		if (device == null) {
			return;
		}
		dialog.show();
		String olsrConfig = OLSRGenerator.getOLSRConfig(device, new OLSRSetting());
		// deploy olsrConfig
		new OLSRConfigDeploy().execute(OLSRD_CONFIG_PATH, olsrConfig, new GenericExecutionCallback() {

			@Override
			public void onUnsucessfullExecution() {
				setStatus("OLSR Config not deplot", false);
			}

			@Override
			public void onSucessfullExecution() {
				// Execute OLSR
				new ExecuteOLSR().execute(OLSRD_PATH, OLSRD_CONFIG_PATH, new GenericExecutionCallback() {

					@Override
					public void onUnsucessfullExecution() {
						dialog.dismiss();
						setStatus("OLSR failed to start", false);
					}

					@Override
					public void onSucessfullExecution() {
						dialog.dismiss();
						setStatus("OLSR started with success", true);
					}
				});
			}
		});
	}

	public void checkOLSRRunning(View v) {
		dialog.show();
		new OLSRRunningTest().execute(new GenericExecutionCallback() {

			@Override
			public void onUnsucessfullExecution() {
				dialog.dismiss();
				setStatus("OLSR isn't running", false);
			}

			@Override
			public void onSucessfullExecution() {
				dialog.dismiss();
				setStatus("OLSR is running", true);
			}
		});
	}

	public void wpa_supplicant(View v) {
		dialog.show();
		if (device == null) {
			// TODO
		}
		try {
			new GenerateWPA_supplicant().execute(device, new Network("Ad-Hoc"), new GenericExecutionCallback() {

				@Override
				public void onUnsucessfullExecution() {
					setStatus("Failed to update wpa_supplicant", false);
					dialog.dismiss();
				}

				@Override
				public void onSucessfullExecution() {
					setStatus("Sucess to update wpa_supplicant", true);
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
			public void onUnsucessfullExecution() {
				dialog.dismiss();
				setStatus("Missing wpa_cli", false);
			}

			@Override
			public void onSucessfullExecution() {
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
				setStatus("Sucessfull Scan", true);
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
				setStatus("Sucessfull deployed wpa_cli", true);

			}
		});
		dialog.show();
	}

	public void removeWPACli(View v) {
		dialog.show();
		new FileRemover().execute(new String[] { WPACLI_PATH }, new GenericExecutionCallback() {

			@Override
			public void onUnsucessfullExecution() {
				dialog.dismiss();
				setStatus("Failed to remove file", false);
			}

			@Override
			public void onSucessfullExecution() {
				dialog.dismiss();
				setStatus("Sucess to remove file", true);
			}
		});
	}

	private void setStatus(String value, boolean sucessfull) {
		status.setText(value);
		status.setBackgroundResource(sucessfull ? R.color.okey : R.color.error);
	}

	private void setStatus(int resourceStringID, boolean sucessfull) {
		status.setText(resourceStringID);
		status.setBackgroundResource(sucessfull ? R.color.okey : R.color.error);
	}

}
