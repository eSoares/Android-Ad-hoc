package pt.it.esoares.android.ip;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Random;

public class IPGenerator {
	private static final String BASE_ADDRESS = "169.254.";
	public static final String NETWORK_MASK = "169.254.0.0";
	public static final int NETWORK_MASK_SIZE = 16;

	/**
	 * Generates a IP Address based on Link-Local Address Selection from RFC 3927
	 * 
	 * @param macAdress
	 *            The MAC Address represented as {@link String} as the following example "2a:3b:4c:5d:6e:70"
	 * @return A {@link Inet4Address} containing the valid generated IP Address
	 * @throws UnknownHostException
	 *             the thrown exception if the generated IP Address is invalid
	 */
	public static Inet4Address generateIP(String macAdress) throws UnknownHostException {
		BigInteger tmp = new BigInteger(macAdress.getBytes());

		Random rand = new Random(tmp.longValue());

		// nexrIntReturns a pseudorandom, uniformly distributed int value between 0 (inclusive) and the specified value (exclusive)
		int ip1 = rand.nextInt(254) + 1; // 1-254
		int ip2 = rand.nextInt(256); // 0-255
		String ip = BASE_ADDRESS + String.valueOf(ip2) + "." + String.valueOf(ip1);
		return (Inet4Address) Inet4Address.getByName(ip);
	}

	public static String getMacAddress() {
		String result = Utils.getMACAddress("wlan0");
		if (result == null || "".equals(result)) {
			result = Utils.getMACAddress("eth0");
		}
		return result;
	}
}
