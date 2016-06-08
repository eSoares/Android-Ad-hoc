package pt.it.esoares.android.olsr;

import android.os.AsyncTask;

import java.util.List;

import eu.chainfire.libsuperuser.Shell.SU;
import pt.it.esoares.android.util.GenericExecutionCallback;

public class OLSRRunningTest extends AsyncTask<Void, Void, Boolean> {

	private GenericExecutionCallback callback;

	@Override
	protected Boolean doInBackground(Void... arg0) {
		return isOLSRRunning();
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

	public void execute(GenericExecutionCallback callback) {
		this.callback = callback;
		this.execute();
	}

	/**
	 * Tests if OLSR is running. This class isn't thread safe!
	 * 
	 * @return if OLSR is running
	 */
	public static boolean isOLSRRunning() {
		List<String> results = SU.run("ps");
		for (String line : results) {
			if (line.contains("/olsrd")) {
				return true;
			}
		}
		return false;
	}
}
