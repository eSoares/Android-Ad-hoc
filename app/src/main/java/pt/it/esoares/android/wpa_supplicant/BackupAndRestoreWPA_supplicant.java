package pt.it.esoares.android.wpa_supplicant;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import pt.it.esoares.android.devices.Device;
import pt.it.esoares.android.devices.DeviceFactory;
import pt.it.esoares.android.util.GenericExecutionCallback;

import java.util.List;

import eu.chainfire.libsuperuser.Shell.SU;

public class BackupAndRestoreWPA_supplicant extends
		AsyncTask<pt.it.esoares.android.wpa_supplicant.BackupAndRestoreWPA_supplicant.Data, Void, Boolean> {
	public final String TAG = getClass().getCanonicalName();
	private String command_toPlace = "cd ";// should append wpa_supplicant location
	private String overwrite_wpa = "cat %s > %s";
	private String bk_file_name = "%s.bk";
	private String bk_wpa_supplicant = "cp %s %s.bk";
	private String clean_backup = "rm %s.bk";
	private String bk_wpa_supplicant_exits = "if [ -e %s.bk ]; then echo 1; else echo 0; fi";

	private GenericExecutionCallback listener;

	@Override
	protected Boolean doInBackground(Data... arg0) {
		if (arg0.length != 1) {
			return false;
		}
		Device device = arg0[0].getDevice();
		Context context = arg0[0].getContext();
		if (device == null) {
			device = DeviceFactory.getDevice(context);
		}
		if (arg0[0].getAction() == Action.BACKUP && !backupAlreadyExists(device)) {
			// backup
			List<String> result = SU.run(new String[]{command_toPlace + device.supplicantLocation(),
					String.format(bk_wpa_supplicant, device.supplicantName(), device.supplicantName())});
			for (String r : result) {
				Log.d(TAG, r);
			}
		} else {
			// restore
			List<String> result = SU.run(new String[]{
					command_toPlace + device.supplicantLocation(),
					String.format(overwrite_wpa, String.format(bk_file_name, device.supplicantName()),
							device.supplicantName()), String.format(clean_backup, device.supplicantName())});
			if(result!=null) {
				for (String r : result) {
					Log.d(TAG, r);
				}
			}

		}
		return true;
	}

	public void backup(Device device, GenericExecutionCallback callback, Context context) {
		this.listener = callback;
		this.execute(new Data(Action.BACKUP, device, context));
	}

	public void restore(Device device, GenericExecutionCallback callback, Context context) {
		this.listener = callback;
		this.execute(new Data(Action.RESTORE, device, context));
	}

	private boolean backupAlreadyExists(Device device) {
		List<String> result = SU.run(String.format(bk_wpa_supplicant_exits, device.supplicantFullQualifiedLocation()));
		if (result == null) {
			return false;
		}
		if (result.get(0).equals("1")) {
			return true;
		}
		return false;
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
		private Context context;

		Data(Action action, Device device, Context context) {
			this.action = action;
			this.device = device;
		}

		public Action getAction() {
			return action;
		}

		public Device getDevice() {
			return device;
		}

		public Context getContext() {
			return context;
		}

	}

	enum Action {
		BACKUP, RESTORE
	}
}
