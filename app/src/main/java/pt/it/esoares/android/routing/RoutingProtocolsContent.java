package pt.it.esoares.android.routing;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.it.esoares.android.ui.Setup;

/**
 * Helper class for providing sample name for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class RoutingProtocolsContent {

	/**
	 * An array of sample (dummy) items.
	 */
	public static final List<RoutingProtocol> ITEMS = new ArrayList<>();

	/**
	 * A map of sample (dummy) items, by ID.
	 */
	public static final Map<String, RoutingProtocol> ITEM_MAP = new HashMap<>();
	private static final int COUNT = 0;
	public static String PROTOCOLS_LOCATION;
	private static OnRoutingProtocolsUpdated listener;

	static {
		// Add some sample items.
		for (int i = 1; i <= COUNT; i++) {
			addItem(createDummyItem(i));
		}
	}

	private static void addItem(RoutingProtocol item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}

	private static RoutingProtocol createDummyItem(int position) {
		return new RoutingProtocol(String.valueOf(position), "Item " + position, true);
	}


	public static void reload(Context context, OnRoutingProtocolsUpdated listener) {
		RoutingProtocolsContent.listener = listener;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		RoutingProtocolsContent.PROTOCOLS_LOCATION = prefs.getString(Setup.CUSTOM_PROTOCOLS_PATH, null);
		new AsyncTask<String, Void, String[]>() {
			@Override
			protected String[] doInBackground(String... params) {
				if (params == null) {
					return null;
				}
				File dir = new File(params[0]);
				return dir.list();
			}

			@Override
			protected void onPostExecute(String[] strings) {
				if (strings == null) {
					return;
				}
				for (int i = 0; i < strings.length; i++) {
					RoutingProtocol protocol = new RoutingProtocol(strings[i], strings[i], false);
					if (!RoutingProtocolsContent.ITEMS.contains(protocol)) {
						RoutingProtocolsContent.ITEMS.add(protocol);
					}
				}

				RoutingProtocolsContent.listener.OnRoutingProtocolsUpdated();
				super.onPostExecute(strings);
			}
		}.execute(RoutingProtocolsContent.PROTOCOLS_LOCATION);
	}

	public interface OnRoutingProtocolsUpdated {
		void OnRoutingProtocolsUpdated();
	}
}
