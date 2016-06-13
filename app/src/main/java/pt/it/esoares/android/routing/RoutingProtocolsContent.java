package pt.it.esoares.android.routing;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pt.it.esoares.android.ui.Setup;

public class RoutingProtocolsContent {

	/**
	 * An array of items.
	 */
	public static final List<RoutingProtocol> ITEMS = new ArrayList<>();

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
	}

	private static RoutingProtocol createDummyItem(int position) {
		return new RoutingProtocol(String.valueOf(position), "Item " + position, true);
	}

	public static void setup(Context c){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		RoutingProtocolsContent.PROTOCOLS_LOCATION = prefs.getString(Setup.CUSTOM_PROTOCOLS_PATH, null);
	}

	public static void reload(Context context, OnRoutingProtocolsUpdated listener) {
		setup(context);
		RoutingProtocolsContent.listener = listener;
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
