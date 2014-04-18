package pt.it.esoares.android.devices;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Network {
	private final static Pattern regex = Pattern
			.compile("(\\w{2}:\\w{2}:\\w{2}:\\w{2}:\\w{2}:\\w{2})\\s+(\\d+)\\s+-(\\d+)\\s*\\[.*\\]\\s*(.+)");

	private String name;
	private String wepKey;
	private int frequency;
	private String bssid;
	private int signal;

	public Network(String name, String wepKey, int frequency) {
		super();
		this.name = name;
		this.wepKey = wepKey;
		this.frequency = frequency;
	}

	private Network(String name, int frequency, int signal, String bssid) {
		super();
		this.name = name;
		this.frequency = frequency;
		this.bssid = bssid;
		this.signal = signal;
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

	public String toSupplicant() {
		String tab = "   ";
		StringBuilder result = new StringBuilder();
		result.append(tab + "ssid=\"" + name + "\"\n");
		result.append(tab + "scan_ssid=1\n");
		result.append(tab + "frequency=" + String.valueOf(frequency) + "\n");
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
		result.append(", Frequency: " + String.valueOf(frequency));
		if (bssid != null && !"".equals(bssid)) {
			result.append(", BSSID: " + bssid);
		}
		if (signal != 0) {
			result.append(", Signal: -" + String.valueOf(signal));
		}

		return result.toString();
	}
}
