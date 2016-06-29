package pt.it.esoares.adhocdroid.devices;

import android.content.Context;

public class DeviceFactory {
	private static Device device;

	public static Device getDevice(Context context) {
		if (device != null) {
			return device;
		}
		Device[] devices = { new DeviceSamsung(context), new DeviceDefault(context) };
		for (Device dev : devices) {
			if (dev.isThisDevice()) {
				if (device == null) {
					synchronized (DeviceFactory.class) {
						if (device == null) {
							device = dev;
						}
					}
				}
				break;
			}
		}
		return device;
	}

	public static Device getDevice(Context context, String uniqID) {
		if(uniqID==null){
			return null;
		}
		if (device != null) {
			return device;
		}
		Device[] devices = { new DeviceSamsung(context), new DeviceDefault(context) };
		for (Device dev : devices) {
			if (dev.getClassUniqIdentifier().equals(uniqID)) {
				if (device == null) {
					synchronized (DeviceFactory.class) {
						if (device == null) {
							device = dev;
						}
					}
				}
				break;
			}
		}
		return device;
	}
}
