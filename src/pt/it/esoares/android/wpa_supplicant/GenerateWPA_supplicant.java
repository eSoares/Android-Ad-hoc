package pt.it.esoares.android.wpa_supplicant;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import eu.chainfire.libsuperuser.Shell.SU;

public abstract class GenerateWPA_supplicant extends AsyncTask<Boolean, Void, Boolean> {
	String command_toPlace = "cd /data/misc/wifi/";
	String overwrite_wpa = "cat %s > wpa_supplicant.conf";
	String temp_file_name = "random_name_here";
	String bk_wpa_supplicant="cp wpa_supplicant.conf wpa_supplicant.conf.bk";
	
	@Override
	protected Boolean doInBackground(Boolean... arg0) {
		if (arg0.length != 1) {
			return false;
		}
		boolean useEncryption = arg0[0];

		StringBuilder config = new StringBuilder();
		config.append("ctrl_interface=wlan0\n");
		config.append("ap_scan=2\n");
		config.append("update_config=1\n");
		config.append("eapol_version=1\n");
		config.append("network={\n");
		config.append(" ssid=\"Ad-hoc\"\n");
		config.append(" scan_ssid=1\n");
		config.append(" frequency=2432\n");
		config.append(" key_mgmt=NONE\n");
		if (useEncryption) {
			config.append(" wep_key0=\"abcde\"\n");
			config.append(" wep_tx_keyidx=0\n");
		}
		config.append(" priority=1\n");
		config.append(" mode=1\n");
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

		List<String> result = SU.run(new String[] { command_toPlace, String.format(overwrite_wpa, f.getAbsolutePath()) });
		for (String r : result) {
			Log.d("OVERWRITE_WPA_SUPPLICANT", r);
		}
		f.delete();
		return true;
	}

	@Override
	protected abstract void onPostExecute(Boolean result);
}
