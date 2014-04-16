package pt.it.esoares.android.devices;

public class DeviceFactory {
	private static Device device;

	public static Device getDevice() {
		if (device != null) {
			return device;
		}
		Device[] devices = { new DeviceSamsung(), new DeviceDefault() };
		for (Device dev : devices) {
			if (dev.isThisDevice()) {
				if (device == null) {
					synchronized (DeviceFactory.class) {
						if (device == null) {
							device = dev;
						}
					}
				}
			}
		}
		return device;
	}
}
