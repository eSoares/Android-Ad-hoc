package pt.it.esoares.android.devices;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Network implements Parcelable {
	public static final int DEFAULT_FREQUENCY = 2432; // channel 5
	private static final String DEFAULT_NAME = "Ad-hoc";

	private static final int[] CHANNELS = { 2412, 2417, 2422, 2427, 2432, 2437, 2442, 2447, 2452, 2457, 2462 }; // only to channel 11

	private final static Pattern regex = Pattern
			.compile("(\\w{2}:\\w{2}:\\w{2}:\\w{2}:\\w{2}:\\w{2})[\\s|\\t]*(\\d+)[\\s|\\t]+-?(\\d+)[\\s|\\t]*\\[.*\\][\\s|\\t]*(.+)");

	private String name;
	private String wepKey;
	private int frequency;
	private String bssid;
	private int signal;

	public Network(String name) throws Exception {
		this(name, null, -1);
	}

	/**
	 * Creates a representation of a Ad-Hoc WiFi network
	 * 
	 * @param name
	 *            the network name
	 * @param wepKey
	 *            the WEP key to use, if don't want to use WEP just pass a null
	 * @param frequency
	 *            if unset pass -1
	 * @throws Exception
	 *             if the frequency is wrong (except when set to -1, in that case will be none)
	 */
	public Network(String name, String wepKey, int frequency) throws Exception {
		super();
		this.name = name;
		this.wepKey = wepKey;
		if (frequency == -1 || isCurrentFrequency(frequency)) {
			this.frequency = frequency;
		} else {
			throw new Exception("Wrong frequency");
		}
	}

	private Network(String name, int frequency, int signal, String bssid) {
		super();
		this.name = name;
		this.frequency = frequency;
		this.bssid = bssid;
		this.signal = signal;
	}

	private Network(Parcel parcel) {
		super();
		this.name = parcel.readString();
		this.wepKey = parcel.readString();
		this.frequency = parcel.readInt();
		this.bssid = parcel.readString();
		this.signal = parcel.readInt();
	}

	private boolean isCurrentFrequency(int frequencyToTest) {
		for (int channelFrequency : CHANNELS) {
			if (frequencyToTest == channelFrequency) {
				return true;
			}
		}
		return false;
	}

	public String getWepKey() {
		return wepKey;
	}

	public void setWepKey(String wepKey) {
		this.wepKey = wepKey;
	}

	public void setNetworkName(String name) {
		this.name = name;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public String getNetworkName() {
		return name;
	}

	public boolean useWEP() {
		return wepKey != null;
	}

	public String WEP_key() {
		return wepKey;
	}

	public String getBssid() {
		return bssid;
	}

	public int getSignal() {
		return signal;
	}

	public int getFrequency() {
		return frequency;
	}

	public boolean hasFrequency() {
		return frequency != -1;
	}

	public String toSupplicant() {
		String tab = "   ";
		StringBuilder result = new StringBuilder();
		result.append(tab + "ssid=\"" + name + "\"\n");
		result.append(tab + "scan_ssid=1\n");
		if (hasFrequency()) {
			result.append(tab + "frequency=" + String.valueOf(frequency) + "\n");
		}
		result.append(tab + "key_mgmt=NONE\n");
		if (useWEP()) {
			result.append(tab + "wep_key0=\"" + wepKey + "\"\n");
			result.append(tab + "wep_tx_keyidx=0\n");
		}
		result.append(tab + "priority=1\n");
		result.append(tab + "mode=1\n");
		return result.toString();
	}

	public static Network parseFromWpaCli(String line) {
		// Example
		// "bssid / frequency / signal level / flags / ssid"
		// "1e:d5:e6:a8:ca:bf	2432	-36	[IBSS]	Ad-hoc"
		// in some devices (like the boston) was found this behaviour
		// a6:ca:26:57:65:73	2432	216	[IBSS]	Ad-hoc
		Matcher matcher = regex.matcher(line);
		if (matcher.matches()) {
			return new Network(matcher.group(4), Integer.parseInt(matcher.group(2)),
					Integer.parseInt(matcher.group(3)), matcher.group(1));
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Name: " + name);
		if (wepKey != null && !"".equals(bssid)) {
			result.append(", Wep Key: " + wepKey);
		}
		if (hasFrequency()) {
			result.append(", Frequency: " + String.valueOf(frequency));
		}
		if (bssid != null && !"".equals(bssid)) {
			result.append(", BSSID: " + bssid);
		}
		if (signal != 0) {
			result.append(", Signal: -" + String.valueOf(signal));
		}

		return result.toString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(wepKey);
		dest.writeInt(frequency);
		dest.writeString(bssid);
		dest.writeInt(signal);
	}

	public static final Parcelable.Creator<Network> CREATOR = new Parcelable.Creator<Network>() {

		@Override
		public Network createFromParcel(Parcel arg0) {
			return new Network(arg0);
		}

		@Override
		public Network[] newArray(int arg0) {
			return new Network[arg0];
		}

	};

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Network)) {
			return false;
		}
		Network comp = (Network) o;
		if ((wepKey == null && comp.wepKey != null) || comp.wepKey == null && wepKey != null) {
			return false;
		}
		if (wepKey != null && wepKey.equals(comp.wepKey)) {
		}
		return comp.name.equals(name) && comp.frequency == frequency;
	}

	public static Network getFromPreferences(SharedPreferences prefs) {
		boolean useWep = prefs.getBoolean("use_wep_checkbox", false);
		String networkName = prefs.getString("network_name_text", DEFAULT_NAME);
		String wepKey = null;
		if (useWep) {
			wepKey = prefs.getString("wep_password_text", null);
		}
		int frequency = Integer.valueOf(prefs.getString("channel_list", String.valueOf(DEFAULT_FREQUENCY)));
		Network network = null;
		try {
			network = new Network(networkName, wepKey, frequency);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (prefs.getBoolean("first_time", true)) {
			Editor editor = prefs.edit();
			editor.putBoolean("first_time", false);
			editor.commit();
			network.saveToPreferences(editor);
		}
		return network;
	}

	public void saveToPreferences(Editor editor) {
		editor.putBoolean("use_wep_checkbox", useWEP());
		if (useWEP()) {
			editor.putString("wep_password_text", getWepKey());
		}
		editor.putString("channel_list", String.valueOf(getFrequency()));
		editor.putString("network_name_text", getNetworkName());
		editor.commit();
	}
}
