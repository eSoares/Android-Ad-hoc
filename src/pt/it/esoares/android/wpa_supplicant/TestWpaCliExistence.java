package pt.it.esoares.android.wpa_supplicant;

import android.os.AsyncTask;

import pt.it.esoares.android.util.GenericExecutionCallback;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class TestWpaCliExistence extends AsyncTask<String, Void, Boolean> {

	private GenericExecutionCallback listener;

	@Override
	protected Boolean doInBackground(String... arg0) {
		if (!Shell.SU.available()) {
			return false;
		}
		if (check("wpa_cli quit" )) {
			return true;
		}
		if (arg0.length < 1) {
			return false;
		}
		for (String location : arg0) {
			if (check(location +" quit")) {
				return true;
			}
		}
		return false;

	}

	public void execute(String[] wpaCliPath, GenericExecutionCallback callback) {
		this.listener = callback;
		this.execute(wpaCliPath);
	}

	private boolean check(String command) {
//		String[] s = { command, "quit" };
		List<String> result = Shell.SU.run(command);
		if (result == null) {
			return false;
		}
		if (result.size() > 0) {
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
}
