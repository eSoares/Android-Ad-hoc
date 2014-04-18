package pt.it.esoares.android.wpa_supplicant;

import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;
import pt.it.esoares.android.devices.Network;

public class ScanExistingNetworks extends
		AsyncTask<String, Network, pt.it.esoares.android.wpa_supplicant.ScanExistingNetworks.Result> {
	private ScanNetworkListener listener;

	public ScanExistingNetworks(ScanNetworkListener listener) {
		super();
		this.listener = listener;
	}

	@Override
	protected Result doInBackground(String... params) {
		if (params.length < 1) {
			return new Result(new ScanNetworksException("Wrong parameter size"));
		}
		String wpa_cli_location = params[0]; // TODO allow more than one place to test
		String[] commandsToRun = { wpa_cli_location + " -p /data/misc/wifi/sockets -i wlan0 SCAN",
				wpa_cli_location + " -p /data/misc/wifi/sockets -i wlan0 SCAN_RESULTS" };
		List<String> results = Shell.SU.run(commandsToRun);
		if (results == null) {
			return new Result(new ScanNetworksException("Error running the commands"));
		}
		if (results.size() < 1 || results.get(0).contains("not found")) {
			return new Result(new ScanNetworksException("wpa_cli not found"));
		}
		// Parse the results
		for (String res : results) {
			Log.d("network results", res);
			if (res.contains("IBSS")) { // is a ad-hoc network
				publishProgress(Network.parseFromWpaCli(res));
			}
		}
		return new Result();
	}

	@Override
	protected void onPostExecute(Result result) {
		if (result.failed()) {
			listener.onEndedWithError(result.getException());
		} else {
			listener.onEnd();
		}
		super.onPostExecute(result);
	}

	@Override
	protected void onProgressUpdate(Network... values) {
		for (Network net : values) {
			if (net != null) {
				listener.onNetworkFound(net);
			}
		}
		super.onProgressUpdate(values);
	}

	/**
	 * Run with default wpa_cli The wpa_cli must be on the path
	 */
	public void run() {
		this.execute("wpa_cli");
	}

	/**
	 * Run with wpa_cli on the given location
	 * 
	 * @param location
	 *            wpa_cli location
	 */
	public void run(String location) {
		this.execute(location);
	}

	class Result {
		private ScanNetworksException exception;

		Result() {
		}

		Result(ScanNetworksException exception) {
			this.exception = exception;
		}

		public boolean failed() {
			return exception != null;
		}

		public ScanNetworksException getException() {
			return exception;
		}
	}

}
