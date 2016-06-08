package pt.it.esoares.android.devices;

public interface Device {

	/**
	 * Returns the wpa_supplicant location
	 * 
	 * @return the location as a full qualified name
	 */
	String supplicantLocation();

	/**
	 * The wpa_supplicant name, in some devices is different
	 * 
	 * @return The wpa_supplicant name
	 */
	String supplicantName();

	/**
	 * return the full qualified location of the supplicant file, should be equal to {@link #supplicantLocation()}+
	 * {@link #supplicantName()}
	 * 
	 * @return
	 */
	String supplicantFullQualifiedLocation();

	/**
	 * Tests if this device is the current where the code is running
	 * 
	 * @return true case this corresponds to the current device
	 */
	boolean isThisDevice();

	/**
	 * Gets the network interface name
	 * 
	 * @return the network interface name
	 */
	String getInterfaceName();

	/**
	 * Returns a unique identifier that can be used in the DeviceFactory to return a new instance of the class later on the live cycle;
	 * 
	 * @return
	 */
	String getClassUniqIdentifier();
}
