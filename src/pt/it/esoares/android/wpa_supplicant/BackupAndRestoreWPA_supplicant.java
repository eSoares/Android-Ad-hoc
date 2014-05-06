package pt.it.esoares.android.wpa_supplicant;

import android.os.AsyncTask;
import android.util.Log;

import pt.it.esoares.android.devices.Device;
import pt.it.esoares.android.util.GenericExecutionCallback;

import java.util.List;

import eu.chainfire.libsuperuser.Shell.SU;

public class BackupAndRestoreWPA_supplicant extends
		AsyncTask<pt.it.esoares.android.wpa_supplicant.BackupAndRestoreWPA_supplicant.Data, Void, Boolean> {
	private String command_toPlace = "cd ";// should append wpa_supplicant location
	private String overwrite_wpa = "cat %s > %s";
	private String bk_file_name = "%s.bk";
	private String bk_wpa_supplicant = "cp %s %s.bk";

	private GenericExecutionCallback listener;

	@Override
	protected Boolean doInBackground(Data... arg0) {
		if (arg0.length != 1) {
			return false;
		}
		Device device = arg0[0].getDevice();
		if (arg0[0].getAction() == Action.BACKUP) {
			// backup
			List<String> result = SU.run(new String[] { command_toPlace + device.supplicantLocation(),
					String.format(bk_wpa_supplicant, device.supplicantName(), device.supplicantName()) });
			for (String r : result) {
				Log.d("OVERWRITE_WPA_SUPPLICANT", r);
			}
		} else {
			// restore
			List<String> result = SU.run(new String[] {
					command_toPlace + device.supplicantLocation(),
					String.format(overwrite_wpa, String.format(bk_file_name, device.supplicantName()),
							device.supplicantName()) });
			for (String r : result) {
				Log.d("OVERWRITE_WPA_SUPPLICANT", r);
			}

		}
		return true;
	}

	public void backup(Device device, GenericExecutionCallback callback) {
		this.listener = callback;
		this.execute(new Data(Action.BACKUP, device));
	}

	public void restore(Device device, GenericExecutionCallback callback) {
		this.listener = callback;
		this.execute(new Data(Action.RESTORE, device));
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			listener.onSucessfullExecution();
		} else {
			listener.onUnsucessfullExecution();
		}
	}

	class Data {
		private Action action;
		private Device device;

		Data(Action action, Device device) {
			this.action = action;
			this.device = device;
		}

		public Action getAction() {
			return action;
		}

		public Device getDevice() {
			return device;
		}

	}

	enum Action {
		BACKUP, RESTORE
	}
}