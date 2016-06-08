package pt.it.esoares.android.wpa_supplicant;

import android.os.AsyncTask;
import android.util.Log;

import pt.it.esoares.android.devices.Device;
import pt.it.esoares.android.devices.Network;
import pt.it.esoares.android.util.GenericExecutionCallback;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import eu.chainfire.libsuperuser.Shell.SU;

public class GenerateWPA_supplicant extends
		AsyncTask<pt.it.esoares.android.wpa_supplicant.GenerateWPA_supplicant.Data, Void, Boolean> {
	private String command_toPlace = "cd /data/misc/wifi/"; //TODO use device info
	private String overwrite_wpa = "cat %s > wpa_supplicant.conf"; //TODO fix name
	private String temp_file_name = "random_name_here";

	private GenericExecutionCallback listener;

	@Override
	protected Boolean doInBackground(Data... arg0) {
		if (arg0.length != 1) {
			return false;
		}
		Device device = arg0[0].getDevice();
		Network network = arg0[0].getNetwork();

		StringBuilder config = new StringBuilder();
		config.append("ctrl_interface=").append(device.getInterfaceName()).append("\n");
		config.append("ap_scan=2\n");
		config.append("update_config=1\n");
		config.append("eapol_version=1\n");
		config.append("network={\n");
		config.append(network.toSupplicant());
		config.append("}\n");

		File f = null;
		try {
			// Environment.getDownloadCacheDirectory();
			f = File.createTempFile(temp_file_name, null);
			FileWriter fileWriter = new FileWriter(f);
			fileWriter.append(config);
			fileWriter.close();
		} catch (Exception e) {
			Log.e("Create WPA Supplicant", e.toString());
			return false;
		}

		List<String> result = SU
				.run(new String[] { command_toPlace, String.format(overwrite_wpa, f.getAbsolutePath()) });
		for (String r : result) {
			Log.d("OVERWRITE_WPA_SUPPLICANT", r);
		}
		f.delete();
		return true;
	}

	public void execute(Device device, Network network, GenericExecutionCallback callback) {
		this.listener = callback;
		this.execute(new Data(network, device));
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			listener.onSuccessfulExecution();
		} else {
			listener.onUnsuccessfulExecution();
		}
	}

	class Data {
		private Network network;
		private Device device;

		Data(Network network, Device device) {
			this.network = network;
			this.device = device;
		}

		public Network getNetwork() {
			return network;
		}

		public Device getDevice() {
			return device;
		}

	}
}
