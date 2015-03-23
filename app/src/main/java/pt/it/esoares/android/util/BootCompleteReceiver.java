package pt.it.esoares.android.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import pt.it.esoares.android.olsr.OLSRKiller;
import pt.it.esoares.android.olsr.OLSRRunningTest;
import pt.it.esoares.android.olsr.OLSRSetting;
import pt.it.esoares.android.ui.Adhoc;
import pt.it.esoares.android.ui.Setup;

public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(arg0);
		final Context context = arg0;
		if (prefs.getBoolean(Adhoc.USE_OLSR, true)) {
			final GenericExecutionCallback emptyCallback = new GenericExecutionCallback() {

				@Override
				public void onUnsucessfullExecution() {
				}

				@Override
				public void onSucessfullExecution() {
				}
			};

			if (prefs.getBoolean(Adhoc.STATE_CONNECTED, false)) {
				new OLSRRunningTest().execute(new GenericExecutionCallback() {

					@Override
					public void onUnsucessfullExecution() {
						// start OLSR
						String olsrConfigPath = prefs.getString(Setup.OLSR_CONFIG_PATH, null);
						String olsrPath = prefs.getString(Setup.OLSR_PATH, null);
						new StartOLSR(olsrConfigPath, olsrPath, emptyCallback).startOlsr(context, new OLSRSetting());
						;
					}

					@Override
					public void onSucessfullExecution() {
					}
				});
			} else {
				new OLSRKiller().execute(emptyCallback);
			}
		}
	}
}
