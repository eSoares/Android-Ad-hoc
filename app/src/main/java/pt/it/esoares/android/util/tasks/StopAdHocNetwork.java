package pt.it.esoares.android.util.tasks;

import android.content.Context;

import pt.it.esoares.android.devices.Device;
import pt.it.esoares.android.ip.Utils;
import pt.it.esoares.android.olsr.OLSRKiller;
import pt.it.esoares.android.util.GenericExecutionCallback;
import pt.it.esoares.android.wpa_supplicant.BackupAndRestoreWPA_supplicant;

public class StopAdHocNetwork {
	Device device;
	Boolean useOLSR;
	GenericExecutionCallback callback;
	Context context;

	public StopAdHocNetwork(Device device, Boolean useOLSR, GenericExecutionCallback callback, Context context) {
		super();
		this.device = device;
		this.useOLSR = useOLSR;
		this.callback = callback;
		this.context = context;
	}

	public void stopNetwork() {
		// first restore current WPA Supplicant
		restoreWPA_Supplicant();
	}

	private void restoreWPA_Supplicant() {
		Utils.changeWifiState(context, false);
		new BackupAndRestoreWPA_supplicant().restore(device, new GenericExecutionCallback() {

			@Override
			public void onUnsuccessfulExecution() {
				callback.onUnsuccessfulExecution();
			}

			@Override
			public void onSuccessfulExecution() {
				if (useOLSR) {
					killOLSR();
				} else {
					callback.onSuccessfulExecution();
				}
			}
		}, context);
	}

	private void killOLSR() {
		new OLSRKiller().execute(new GenericExecutionCallback() {

			@Override
			public void onUnsuccessfulExecution() {
				callback.onUnsuccessfulExecution();
			}

			@Override
			public void onSuccessfulExecution() {
				callback.onSuccessfulExecution();
			}
		});
	}
}
