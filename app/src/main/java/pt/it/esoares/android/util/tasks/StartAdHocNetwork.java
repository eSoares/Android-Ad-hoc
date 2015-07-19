package pt.it.esoares.android.util.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.net.InetAddress;
import java.net.UnknownHostException;

import pt.it.esoares.android.devices.Device;
import pt.it.esoares.android.devices.Network;
import pt.it.esoares.android.ip.IPInfo;
import pt.it.esoares.android.ip.Utils;
import pt.it.esoares.android.olsr.ExecuteOLSR;
import pt.it.esoares.android.olsr.OLSRConfigDeploy;
import pt.it.esoares.android.olsr.OLSRGenerator;
import pt.it.esoares.android.olsr.OLSRSetting;
import pt.it.esoares.android.util.GenericExecutionCallback;
import pt.it.esoares.android.wpa_supplicant.BackupAndRestoreWPA_supplicant;
import pt.it.esoares.android.wpa_supplicant.GenerateWPA_supplicant;
import pt.it.porto.esoares.android.network.ip.IPManager;
import pt.it.porto.esoares.android.network.ip.IPManagerFactory;

public class StartAdHocNetwork {
	Device device;
	Network network;
	IPInfo ipInfo;
	String olsrConfigPath;
	String olsrExecutionPath;
	Boolean useOLSR;
	GenericExecutionCallback callback;
	Context context;

	public StartAdHocNetwork(Device device, Network network, IPInfo ipInfo, String olsrConfigPath,
			String olsrExecutionPath, Boolean useOLSR, GenericExecutionCallback callback, Context context) {
		super();
		this.device = device;
		this.network = network;
		this.ipInfo = ipInfo;
		this.olsrConfigPath = olsrConfigPath;
		this.olsrExecutionPath = olsrExecutionPath;
		this.useOLSR = useOLSR;
		this.callback = callback;
		this.context = context;
	}

	public void startNetwork() {
		// first backup current WPA Supplicant
		backupWPA_Supplicant();
	}

	private void backupWPA_Supplicant() {
		new BackupAndRestoreWPA_supplicant().backup(device, new GenericExecutionCallback() {

			@Override
			public void onUnsucessfullExecution() {
				callback.onUnsucessfullExecution();
			}

			@Override
			public void onSucessfullExecution() {
				// deploy wpa supplicant
				deployWPA_Supplicant();
			}
		}, context);
	}

	private void deployWPA_Supplicant() {
		Utils.changeWifiState(context, false);
		// deploy WPA supplicant
		new GenerateWPA_supplicant().execute(device, network, new GenericExecutionCallback() {

			@Override
			public void onUnsucessfullExecution() {
				callback.onUnsucessfullExecution();
			}

			@Override
			public void onSucessfullExecution() {
				Utils.changeWifiState(context, true);
				// set ip
				if (!setIP()) {
					return; // returns in case of error
				} else {
					if (useOLSR) {
						setupOLSR();
					} else {
						callback.onSucessfullExecution();
					}
				}
			}

		});
	}

	private boolean setIP() {
		IPManager ipManager = IPManagerFactory.getIPManager(context);
		try {
			ipManager.setIP(InetAddress.getByName(ipInfo.getIpAddress()), ipInfo.getMask(),
					InetAddress.getByName(ipInfo.getGateway()), InetAddress.getByName(ipInfo.getDnsServer()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
			callback.onUnsucessfullExecution();
			return false;
		}
		return true;
	}

	private void setupOLSR() {
		new AsyncTask<OLSRSetting, Void, String>() {

			@Override
			protected String doInBackground(OLSRSetting... params) {
				return OLSRGenerator.getOLSRConfig(device, params.length != 0 ? params[0] : new OLSRSetting());
			}

			protected void onPostExecute(String result) {
				// deploy olsrConfig
				new OLSRConfigDeploy().execute(olsrConfigPath, result, new GenericExecutionCallback() {

					@Override
					public void onUnsucessfullExecution() {
						callback.onUnsucessfullExecution();
					}

					@Override
					public void onSucessfullExecution() {
						// Execute OLSR
						new ExecuteOLSR().execute(olsrExecutionPath, olsrConfigPath, new GenericExecutionCallback() {

							@Override
							public void onUnsucessfullExecution() {
								callback.onUnsucessfullExecution();
							}

							@Override
							public void onSucessfullExecution() {
								callback.onSucessfullExecution();
							}
						});
					}
				});

			};

		}.execute(new OLSRSetting());
	}
}
