package pt.it.esoares.android.routing;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

public class StatusStorage {
	private static final String TAG = "StatusStorage";

	private static final String PROTOCOLS_PREFIX = "PROTOCOL_";

	public static void saveProtocolsStatus(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		Set<String> protocols = new HashSet<>(RoutingProtocolsContent.ITEMS.size());
		for (int i = 0; i < RoutingProtocolsContent.ITEMS.size(); i++) {
			RoutingProtocolsContent.RoutingProtocol p = RoutingProtocolsContent.ITEMS.get(i);
			protocols.add(p.name);
			editor.putBoolean(PROTOCOLS_PREFIX + p.name, p.isRunning);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			editor.putStringSet(PROTOCOLS_PREFIX, protocols);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			editor.apply();
		} else {
			editor.commit();
		}
	}

	public static void loadProtocolsStatus(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		for (int i = 0; i < RoutingProtocolsContent.ITEMS.size(); i++) {
			RoutingProtocolsContent.RoutingProtocol p = RoutingProtocolsContent.ITEMS.get(i);
			if (prefs.contains(PROTOCOLS_PREFIX + p.name)) {
				p.isRunning = prefs.getBoolean(PROTOCOLS_PREFIX + p.name, false);
			}
		}
	}
}
