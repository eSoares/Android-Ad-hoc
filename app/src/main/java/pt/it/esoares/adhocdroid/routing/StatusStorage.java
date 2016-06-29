package pt.it.esoares.adhocdroid.routing;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import java.util.Arrays;
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
			RoutingProtocol p = RoutingProtocolsContent.ITEMS.get(i);
			protocols.add(p.name);
			editor.putBoolean(PROTOCOLS_PREFIX + p.name, p.isRunning.get());
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			editor.putStringSet(PROTOCOLS_PREFIX, protocols);
		} else {
			editor.putString(PROTOCOLS_PREFIX, Arrays.toString(protocols.toArray(new String[protocols.size()])));
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
			RoutingProtocol p = RoutingProtocolsContent.ITEMS.get(i);
			if (prefs.contains(PROTOCOLS_PREFIX + p.name)) {
				p.isRunning.set(prefs.getBoolean(PROTOCOLS_PREFIX + p.name, false));
			}
		}
	}

	public static String[] getProtocolsList(Context c) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		String[] result = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			Set<String> p = prefs.getStringSet(PROTOCOLS_PREFIX, null);
			if (p != null) {
				result = p.toArray(new String[p.size()]);
			}
		} else {
			String ps = prefs.getString(PROTOCOLS_PREFIX, null);
			if (ps != null) {
				result = ps.split(", ");
			}
		}
		return result;
	}

	public static boolean isRunning(Context c, String protocol) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		return prefs.getBoolean(PROTOCOLS_PREFIX + protocol, false);
	}
}
