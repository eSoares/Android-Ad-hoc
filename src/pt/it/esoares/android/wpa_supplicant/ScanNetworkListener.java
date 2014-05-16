package pt.it.esoares.android.wpa_supplicant;

import pt.it.esoares.android.devices.Network;

public interface ScanNetworkListener {

	public void onNetworkFound(Network network);

	public void onEndedWithError(ScanNetworksException scanNetworksException);

	public void onEnd();

}
