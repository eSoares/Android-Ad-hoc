package pt.it.esoares.android.wpa_supplicant;

import pt.it.esoares.android.devices.Network;

public interface ScanNetworkListener {

	void onNetworkFound(Network network);

	void onEndedWithError(ScanNetworksException scanNetworksException);

	void onEnd();

}
