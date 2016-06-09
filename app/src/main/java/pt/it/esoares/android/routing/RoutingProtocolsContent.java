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

	static {
		// Add some sample items.
		for (int i = 1; i <= COUNT; i++) {
			addItem(createDummyItem(i));
		}
	}

	private static OnRoutingProtocolsUpdated listener;

	private static void addItem(RoutingProtocol item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}

	private static RoutingProtocol createDummyItem(int position) {
		return new RoutingProtocol(String.valueOf(position), "Item " + position, true);
	}


	public static void reload(Context context, final OnRoutingProtocolsUpdated listener ){
		RoutingProtocolsContent.listener=listener;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
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
					RoutingProtocol protocol = new RoutingProtocol(strings[i], strings[i], true);
					if (!RoutingProtocolsContent.ITEMS.contains(protocol)) {
						RoutingProtocolsContent.ITEMS.add(protocol);
					}
				}

				listener.OnRoutingProtocolsUpdated();
				super.onPostExecute(strings);
			}
		}.execute(prefs.getString(Setup.CUSTOM_PROTOCOLS_PATH, null));
	}

	/**
	 * A dummy item representing a piece of name.
	 */
	public static class RoutingProtocol {
		public final String id;
		public final String name;
		public final boolean hasSettings;
		public boolean isRunning=false;

		public RoutingProtocol(String id, String name, boolean hasSettings) {
			this.id = id;
			this.name = name;
			this.hasSettings = hasSettings;
		}

		@Override
		public String toString() {
			return "RoutingProtocol{" +
					"id='" + id + '\'' +
					", name='" + name + '\'' +
					", hasSettings=" + hasSettings +
					", isRunning=" + isRunning +
					'}';
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			RoutingProtocol that = (RoutingProtocol) o;

			if (hasSettings != that.hasSettings) {
				return false;
			}
			if (id != null ? !id.equals(that.id) : that.id != null) {
				return false;
			}
			return name != null ? name.equals(that.name) : that.name == null;

		}

		@Override
		public int hashCode() {
			int result = id != null ? id.hashCode() : 0;
			result = 31 * result + (name != null ? name.hashCode() : 0);
			result = 31 * result + (hasSettings ? 1 : 0);
			return result;
		}
	}

	public interface OnRoutingProtocolsUpdated{
		void OnRoutingProtocolsUpdated();
	}
}
