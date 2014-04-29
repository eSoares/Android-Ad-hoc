package pt.it.esoares.android.devices;

public interface Device {

	/**
	 * Returns the wpa_supplicant location 
	 * @return the location as a full qualified name
	 */
	public String supplicantLocation();
	
	/**
	 * The wpa_supplicant name, in some devices is different
	 * @return The wpa_supplicant name
	 */
	public String supplicantName();
	
	/**
	 * return the full qualified location of the supplicant file, should be equall to {@link #supplicantLocation()}+{@link #supplicantName()}
	 * @return
	 */
	public String supplicantFullQualifiedLocation();
	
	/**
	 * Tests if this device is the current where the code is running
	 * @return true case this corresponds to the current device
	 */
	boolean isThisDevice();
	
	/**
	 * Gets the network interface name
	 * @return the network interface name
	 */
	public String getInterfaceName();
}
