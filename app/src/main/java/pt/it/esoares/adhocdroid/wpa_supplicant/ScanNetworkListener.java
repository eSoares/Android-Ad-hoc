package pt.it.esoares.adhocdroid.wpa_supplicant;

import pt.it.esoares.adhocdroid.devices.Network;

public interface ScanNetworkListener {

	void onNetworkFound(Network network);

	void onEndedWithError(ScanNetworksException scanNetworksException);

	void onEnd();

}
