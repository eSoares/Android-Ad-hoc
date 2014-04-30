package pt.it.esoares.android.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pt.it.esoares.android.devices.Network;
import pt.it.esoares.android.ip.IPInfo;
import pt.it.esoares.android.olsrdeployer.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class InfoFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static final String ARG_IP_ADDRESS = "ip_address";
	private static final String ARG_NETWORK = "network";

	private static Adhoc activity;

	private Network network;
	private IPInfo ipInfo;

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static InfoFragment newInstance(int sectionNumber, Network networkInfo, IPInfo ipInfo) {
		InfoFragment fragment = new InfoFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		args.putParcelable(ARG_IP_ADDRESS, ipInfo);
		args.putParcelable(ARG_NETWORK, networkInfo);
		fragment.setArguments(args);
		return fragment;
	}

	public InfoFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_adhoc, container, false);
		Bundle arguments = getArguments() != null ? getArguments() : savedInstanceState;
		if (arguments != null) {
			refreshNetworkUI(rootView);
			setIP((IPInfo) arguments.getParcelable(ARG_IP_ADDRESS), rootView);
			setNetwork((Network) arguments.getParcelable(ARG_NETWORK), rootView);
		}
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshNetworkUI(null);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	public void refreshNetworkUI(View v) {
		View view = getViewRoot(v);
		if (view == null || network == null) {
			return;
		}
		((TextView) view.findViewById(R.id.txt_Network_Name)).setText(network.getNetworkName());
		((TextView) view.findViewById(R.id.txt_Frequency)).setText(String.valueOf(network.getFrequency()));
		((TextView) view.findViewById(R.id.txt_Protection)).setText(network.useWEP() ? R.string.txt_protection_wep
				: R.string.txt_protection_none);
		((TextView) view.findViewById(R.id.txt_Password)).setText(network.useWEP() ? network.getWepKey() : "None");
	}

	public void setIPAddress(String ipAddress) {
		((TextView) getView().findViewById(R.id.txt_IP)).setText(ipAddress);
	}

	private void setNetwork(Network network, View rootView) {
		this.network = network;
		refreshNetworkUI(rootView);

	}

	public void setNetwork(Network network) {
		setNetwork(network, getViewRoot(null));
	}

	private void setIP(IPInfo parcelable, View rootView) {
		this.ipInfo = ipInfo;
		if (ipInfo != null) {
			((TextView) rootView.findViewById(R.id.txt_IP)).setText(ipInfo.getIpAddress());
		}
	}

	public void setIP(IPInfo ipInfo) {
		View v = getViewRoot(null);
		setIP(ipInfo, v);
	}

	private View getViewRoot(View v) {
		return v == null ? getView() : v;
	}

}
