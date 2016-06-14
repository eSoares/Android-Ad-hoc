package pt.it.esoares.android.util.tasks;

import android.content.Context;

import java.net.InetAddress;
import java.net.UnknownHostException;

import pt.it.esoares.android.devices.Device;
import pt.it.esoares.android.devices.Network;
import pt.it.esoares.android.ip.IPInfo;
import pt.it.esoares.android.ip.Utils;
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
	GenericExecutionCallback callback;
	Context context;

	public StartAdHocNetwork(Device device, Network network, IPInfo ipInfo, String olsrConfigPath,
							 String olsrExecutionPath, GenericExecutionCallback callback, Context context) {
		super();
		this.device = device;
		this.network = network;
		this.ipInfo = ipInfo;
		this.olsrConfigPath = olsrConfigPath;
		this.olsrExecutionPath = olsrExecutionPath;
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
			public void onUnsuccessfulExecution() {
				callback.onUnsuccessfulExecution();
			}

			@Override
			public void onSuccessfulExecution() {
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
			public void onUnsuccessfulExecution() {
				callback.onUnsuccessfulExecution();
			}

			@Override
			public void onSuccessfulExecution() {
				Utils.changeWifiState(context, true);
				// set ip
				if (!setIP()) {
					return; // returns in case of error
				} else {
					callback.onSuccessfulExecution();
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
			callback.onUnsuccessfulExecution();
			return false;
		}
		return true;
	}
}
