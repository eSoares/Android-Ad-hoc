package pt.it.esoares.android.devices;

import java.util.List;

import eu.chainfire.libsuperuser.Shell.SU;

class DeviceSamsung implements Device {
	private static final String SUPPLICANT_LOCATION = "/data/wifi/";
	private static final String SUPPLICANT_NAME = "bcm_supp.conf";
	private static final String TEST_EXISTENCE = "if [ -e /data/wifi/bcm_supp.conf ]; then echo 1; else echo 0; fi";
	private static final String INTERFACE_NAME = "ip link show";
	private static final String IDENTIFIER="identifier samsung";
	private String interfaceName;
	
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
			_getInterfaceName();
			return true;
		}
		return false;
	}

	@Override
	public String supplicantFullQualifiedLocation() {
		return SUPPLICANT_LOCATION + SUPPLICANT_NAME;
	}

	@Override
	public String getInterfaceName() {
		if(interfaceName==null){
			_getInterfaceName();
		}
		return interfaceName;
	}
	
	private void _getInterfaceName(){
		List<String> result = SU.run(INTERFACE_NAME);
		for (String res : result) {
			if (res.contains("wlan0")) {
				interfaceName="wlan0";
			}
			if (res.contains("eth0")) { 
				interfaceName= "eth0";
			}
		}
		
	}

	@Override
	public String getClassUniqIdentifier() {
		return IDENTIFIER;
	}

}
