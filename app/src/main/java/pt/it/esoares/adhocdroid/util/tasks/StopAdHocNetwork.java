package pt.it.esoares.adhocdroid.util.tasks;

import android.content.Context;

import pt.it.esoares.adhocdroid.devices.Device;
import pt.it.esoares.adhocdroid.ip.Utils;
import pt.it.esoares.adhocdroid.util.GenericExecutionCallback;
import pt.it.esoares.adhocdroid.wpa_supplicant.BackupAndRestoreWPA_supplicant;

public class StopAdHocNetwork {
	Device device;
	GenericExecutionCallback callback;
	Context context;

	public StopAdHocNetwork(Device device, GenericExecutionCallback callback, Context context) {
		super();
		this.device = device;
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
					callback.onSuccessfulExecution();
			}
		}, context);
	}

}
