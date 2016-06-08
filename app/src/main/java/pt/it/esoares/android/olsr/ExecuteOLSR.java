package pt.it.esoares.android.olsr;

import android.os.AsyncTask;

import pt.it.esoares.android.util.GenericExecutionCallback;

import eu.chainfire.libsuperuser.Shell.SU;

public class ExecuteOLSR extends AsyncTask<String, Void, Boolean> {
	private GenericExecutionCallback callback;

	@Override
	protected Boolean doInBackground(String... params) {
		if (params.length < 2) {
			return false;
		}
		String olsrPath = params[0];
		String olsrConfigPath = params[1];
		SU.run(olsrPath + " -f " + olsrConfigPath +" -d 0");
		// Verifies that olsr is running
		try {
			Thread.sleep(100 * 5);// 5 seconds
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return OLSRRunningTest.isOLSRRunning();
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			callback.onSuccessfulExecution();
		} else {
			callback.onUnsuccessfulExecution();
		}
		super.onPostExecute(result);
	}

	public void execute(String olsrPath, String olsrConfigPath, GenericExecutionCallback callback) {
		this.callback = callback;
		this.execute(olsrPath, olsrConfigPath);
	}
}
