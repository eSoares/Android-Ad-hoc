package pt.it.esoares.adhocdroid.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import pt.it.esoares.adhocdroid.routing.RoutingProtocolsContent;
import pt.it.esoares.adhocdroid.routing.StatusStorage;
import pt.it.esoares.adhocdroid.util.tasks.RoutingProtocolStartStop;
import pt.it.esoares.adhocdroid.util.tasks.StartNetwork;

public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(arg0);
		final Context context = arg0;
		if (prefs.getBoolean("auto_star_onboot", false)) { // only start if auto start on boot is on
			RoutingProtocolsContent.setup(context);
			new StartNetwork(arg0.getApplicationContext()).execute(new GenericExecutionCallback() {
				@Override
				public void onSuccessfulExecution() {
					String[] protocols = StatusStorage.getProtocolsList(context);
					List<String> runningProtocols = new ArrayList<>(protocols.length);
					for (int i = 0; i < protocols.length; i++) {
						if (StatusStorage.isRunning(context, protocols[i])) {
							runningProtocols.add(protocols[i]);
						}
					}
					if (runningProtocols.size() > 0) {
						// Stops each routing protocol in order to clean any temporary state
						// then starts again the routing protocol
						// Note: the order of starting multiple routing protocols is not recorder, maybe it should?
						RoutingProtocolStartStop.restartRoutingProtocols(runningProtocols.toArray(new String[runningProtocols.size()]));
					}
				}

				@Override
				public void onUnsuccessfulExecution() {
				}
			}, context);

			BatteryUpdateReceiver batteryLog = new BatteryUpdateReceiver();
			arg0.getApplicationContext().registerReceiver(batteryLog, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		}
	}
}
