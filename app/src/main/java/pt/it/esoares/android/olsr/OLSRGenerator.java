package pt.it.esoares.android.olsr;

import pt.it.esoares.android.devices.Device;

public class OLSRGenerator {

	public static String getOLSRConfig(Device device, OLSRSetting setting) {

		StringBuilder result = new StringBuilder();
		result.append("FIBMetric \"flat\"\n");
		result.append("ClearScreen yes\n");
		result.append("AllowNoInt yes\n");
		result.append("IpcConnect{\n");
		result.append("	MaxConnections 0\n");
		result.append("	Host 127.0.0.1\n}");
		result.append("UseHysteresis no\n" + "NicChgsPollInt 3.0\n" + "TcRedundancy 2\n" + "MprCoverage 3\n");
		
		// plugin
		if (setting.useTXTInfo()) {
			result.append("LoadPlugin \"").append(setting.pluginTXTInfoLocation()).append("\"\n");
			result.append("{\n");
			result.append("	PlParam \"Accept\" \"127.0.0.1\"");
			result.append("}\n");
		}

		// Interface
		result.append("Interface \"").append(device.getInterfaceName()).append("\"\n");
		result.append("{\n	 Ip4Broadcast 255.255.255.255\n");
		result.append("	 Mode \"mesh\"\n}\n");

		return result.toString();
	}
}
