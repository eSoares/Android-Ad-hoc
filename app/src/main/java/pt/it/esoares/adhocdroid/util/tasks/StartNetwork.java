package pt.it.esoares.adhocdroid.util.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import java.io.IOException;

import pt.it.esoares.adhocdroid.devices.Device;
import pt.it.esoares.adhocdroid.devices.DeviceFactory;
import pt.it.esoares.adhocdroid.devices.Network;
import pt.it.esoares.adhocdroid.ip.IPInfo;
import pt.it.esoares.adhocdroid.ui.Adhoc;
import pt.it.esoares.adhocdroid.ui.Setup;
import pt.it.esoares.adhocdroid.util.GenericExecutionCallback;

public class StartNetwork extends AsyncTask<Context, Void, Device> {

	private Context context;
	private GenericExecutionCallback callback = null;

	public StartNetwork(Context context) {
		this.context = context;
	}

	@Override
	protected Device doInBackground(Context... params) {
		return DeviceFactory.getDevice(params[0]);
	}

	@Override
	protected void onPostExecute(Device result) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String olsrConfigPath = prefs.getString(Setup.SDCARD_PROTOCOLS_PATH, null);
		String olsrPath = prefs.getString(Setup.CUSTOM_PROTOCOLS_PATH, null);
		Network network = Network.getFromPreferences(prefs);
		IPInfo ipInfo = null;
		try {
			ipInfo = IPInfo.getFromPreferences(prefs);
		} catch (IOException e) {
			e.printStackTrace();
		}
		boolean useOLSR = prefs.getBoolean("use_olsr", true);
		new StartAdHocNetwork(result, network, ipInfo, olsrConfigPath, olsrPath, callback,
				context).startNetwork();
		saveState(true, false, useOLSR);
		super.onPostExecute(result);
	}

	public void execute(GenericExecutionCallback callback, Context context) {
		this.callback = callback;
		saveState(false, true, false);
		this.execute(context);
	}

	private void saveState(boolean connected, boolean connecting, boolean olsr_connected){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(Adhoc.STATE_CONNECTED, connected);
		editor.putBoolean(Adhoc.STATE_CONNECTING, connecting);
		editor.putBoolean(Adhoc.STATE_OLSR, olsr_connected);
		editor.commit();
	}
}
