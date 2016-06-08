package pt.it.esoares.android.olsr;

import android.os.AsyncTask;

import pt.it.esoares.android.util.GenericExecutionCallback;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class TestOLSRExistence extends AsyncTask<String, Void, Boolean> {

	private GenericExecutionCallback listener;

	@Override
	protected Boolean doInBackground(String... arg0) {
		if (!Shell.SU.available()) {
			return false;
		}
		if (arg0.length < 1) {
			return false;
		}
		for (String location : arg0) {
			if (check(location)) {
				return true;
			}
		}
		// TODO test if the file while can't be executed is there, make a proper callback for this
		return false;

	}

	public void execute(String olsrPath, GenericExecutionCallback callback) {
		this.listener = callback;
		this.execute(olsrPath);
	}

	private boolean check(String command) {
		List<String> result = Shell.SU.run(command);
		if (result.size() > 3) {
			return true;
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			listener.onSuccessfulExecution();
		} else {
			listener.onUnsuccessfulExecution();
		}
	}
}
