package pt.it.esoares.android.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import pt.it.esoares.android.olsr.OLSRSetting;
import pt.it.esoares.android.ui.Adhoc;
import pt.it.esoares.android.ui.Setup;
import pt.it.esoares.android.util.tasks.StartNetwork;
import pt.it.esoares.android.util.tasks.StartOLSR;

public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(arg0);
		final Context context = arg0;

		new StartNetwork(arg0.getApplicationContext()).execute(new GenericExecutionCallback() {

			@Override
			public void onSuccessfulExecution() {
				if (prefs.getBoolean(Adhoc.USE_OLSR, true)) {
//					 start OLSR
					String olsrConfigPath = prefs.getString(Setup.OLSR_CONFIG_PATH, null);
					String olsrPath = prefs.getString(Setup.OLSR_PATH, null);
					new StartOLSR(olsrConfigPath, olsrPath, GenericExecutionCallback.getEmptyCallback()).startOlsr(context, new OLSRSetting());
				}
			}

			@Override
			public void onUnsuccessfulExecution() {
			}
		}, arg0);

		BatteryUpdateReceiver batteryLog = new BatteryUpdateReceiver();
		arg0.getApplicationContext().registerReceiver(batteryLog, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	}
}
