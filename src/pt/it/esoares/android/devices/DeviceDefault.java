package pt.it.esoares.android.devices;

import java.util.List;

import eu.chainfire.libsuperuser.Shell.SU;

class DeviceDefault implements Device {
	private static final String SUPPLICANT_LOCATION = "/data/misc/wifi/";
	private static final String SUPPLICANT_NAME = "wpa_supplicant.conf";
	private static final String TEST_EXISTENCE = "if [ -e /data/misc/wifi/wpa_supplicant.conf ]; then echo 1; else echo 0; fi";
	private static final String INTERFACE_NAME = "ip link show";

	@Override
	public String supplicantLocation() {
		return SUPPLICANT_LOCATION;
	}

	@Override
	public String supplicantName() {
		return SUPPLICANT_NAME;
	}

	@Override
	public boolean isThisDevice() {
		List<String> result = SU.run(TEST_EXISTENCE);
		if (result == null) {
			return false;
		}
		if (result.get(0).equals("1")) {
			return true;
		}
		return false;
	}

	@Override
	public String supplicantFullQualifiedLocation() {
		return SUPPLICANT_LOCATION + SUPPLICANT_NAME;
	}

	@Override
	public String interfaceName() {
		List<String> result = SU.run(INTERFACE_NAME);
		for (String res : result) {
			if (res.contains("wlan0")) {
				return "wlan0";
			}
			if (res.contains("eth0")) {
				return "eth0";
			}
		}
		return null;
	}

}
