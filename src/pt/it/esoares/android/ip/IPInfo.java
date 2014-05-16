package pt.it.esoares.android.ip;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcel;
import android.os.Parcelable;

import pt.it.esoares.android.ui.Setup;

import java.io.IOException;

public class IPInfo implements Parcelable {

	private String ipAddress;
	private int mask;
	private String gateway;
	private String dnsServer;
	private boolean static_ip;

	public IPInfo(String ipAddress, int mask, String gateway, String dnsServer, boolean b) {
		super();
		this.ipAddress = ipAddress;
		this.mask = mask;
		this.gateway = gateway;
		this.dnsServer = dnsServer;
		this.static_ip = b;
	}

	private IPInfo(Parcel parcel) {
		super();
		String[] content = new String[3];
		parcel.readStringArray(content);
		this.ipAddress = content[0];
		this.gateway = content[1];
		this.dnsServer = content[2];
		this.mask = parcel.readInt();
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getMask() {
		return mask;
	}

	public void setMask(int mask) {
		this.mask = mask;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public String getDnsServer() {
		return dnsServer;
	}

	public void setDnsServer(String dnsServer) {
		this.dnsServer = dnsServer;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeStringArray(new String[] { ipAddress, gateway, dnsServer });
		arg0.writeInt(mask);
	}

	public static final Parcelable.Creator<IPInfo> CREATOR = new Parcelable.Creator<IPInfo>() {

		@Override
		public IPInfo createFromParcel(Parcel arg0) {
			return new IPInfo(arg0);
		}

		@Override
		public IPInfo[] newArray(int arg0) {
			return new IPInfo[arg0];
		}

	};

	public static IPInfo getFromPreferences(SharedPreferences prefs) throws IOException {
		boolean static_ip = prefs.getBoolean("use_static_ip_pref", false);
		String ip_address = null;
		int mask = 0;
		String gateway = null;
		IPInfo ip;
		if (static_ip) {
			ip_address = prefs.getString("ip_address", null);
			if (ip_address == null) {
				ip_address = IPGenerator.generateIP(prefs.getString(Setup.MAC_ADDRESS, "00:00")).getHostAddress();
			}
			mask = Integer.valueOf(prefs.getString("ip_mask", String.valueOf(IPGenerator.NETWORK_MASK_SIZE)));
			gateway = prefs.getString("ip_address_gateway", IPGenerator.RESERVED_ADDRESS);
			ip = new IPInfo(ip_address, mask, gateway, IPGenerator.DNS_SERVER, true);

		} else {
			ip_address = IPGenerator.generateIP(prefs.getString(Setup.MAC_ADDRESS, "00:00")).getHostAddress();
			mask = IPGenerator.NETWORK_MASK_SIZE;
			gateway = IPGenerator.RESERVED_ADDRESS;
			ip = new IPInfo(ip_address, mask, gateway, IPGenerator.DNS_SERVER, false);
			ip.saveToPreferences(prefs.edit());
		}
		return ip;

	}

	public void saveToPreferences(Editor editor) {
		editor.putBoolean("use_static_ip_pref", useStaticIP());
		editor.putString("ip_address", getIpAddress());
		editor.putString("ip_mask", String.valueOf(getMask()));
		editor.putString("ip_address_gateway", getGateway());
		editor.commit();
	}

	private boolean useStaticIP() {
		return this.static_ip;
	}
}
