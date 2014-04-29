package pt.it.esoares.android.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pt.it.esoares.android.devices.Network;
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

	private static Adhoc activity;

	private static Network network;

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static InfoFragment newInstance(int sectionNumber, Network networkInfo, String ipAddress) {
		InfoFragment fragment = new InfoFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		args.putString(ARG_IP_ADDRESS, ipAddress);
		fragment.setArguments(args);
		fragment.network=networkInfo;
		return fragment;
	}

	public InfoFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_adhoc, container, false);
		Bundle arguments = getArguments();
		if (arguments != null) {
			refreshNetworkUI(rootView);
			String ip = arguments.getString(ARG_IP_ADDRESS);
			if (ip != null) {
				((TextView) rootView.findViewById(R.id.txt_IP)).setText(ip);
			}
		}
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshNetworkUI(getView());
	}

	public void refreshNetworkUI(View v) {
		View view = v == null ? getView() : v;
		if (view == null) {
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

	public void setNetwork(Network network) {
		this.network = network;
		refreshNetworkUI(getView());
	}

}
