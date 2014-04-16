package pt.it.esoares.android.devices;

public class Network {
	private String name;
	private String wepKey;
	private int frequency;

	public Network(String name, String wepKey, int frequency) {
		super();
		this.name = name;
		this.wepKey = wepKey;
		this.frequency = frequency;
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
}
