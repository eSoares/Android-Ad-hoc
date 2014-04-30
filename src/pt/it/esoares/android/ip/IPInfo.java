package pt.it.esoares.android.ip;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import pt.it.esoares.android.devices.Network;

import java.net.UnknownHostException;

public class IPInfo implements Parcelable {

	private String ipAddress;
	private int mask;
	private String gateway;
	private String dnsServer;

	public IPInfo(String ipAddress, int mask, String gateway, String dnsServer) {
		super();
		this.ipAddress = ipAddress;
		this.mask = mask;
		this.gateway = gateway;
		this.dnsServer = dnsServer;
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

	public static IPInfo getFromPreferences(SharedPreferences prefs, Context context) {
		boolean useStaticIP = prefs.getBoolean("use_static_ip_pref", false);
		String ip = null;
		if (useStaticIP) {
			ip = prefs.getString("ip_address", null);
		}
		if (ip == null) {// use generated IP
			Utils.changeWifiState(context, true);
			try {
				ip = IPGenerator.generateIP(IPGenerator.getMacAddress()).getHostAddress();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			return new IPInfo(ip, IPGenerator.NETWORK_MASK_SIZE, IPGenerator.RESERVED_ADDRESS, IPGenerator.DNS_SERVER);
		} else {
			int mask = Integer.valueOf(prefs.getString("ip_mask", String.valueOf(IPGenerator.NETWORK_MASK_SIZE)));
			String gateway = prefs.getString("ip_address_gateway", IPGenerator.RESERVED_ADDRESS); // this can be trouble
			return new IPInfo(ip, mask, gateway, IPGenerator.DNS_SERVER);
		}
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
}
