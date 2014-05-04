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
 * The Fragment to show information of the network and start it!
 */
public class InfoFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	private static Adhoc activity;

	private Network network;
	private IPInfo ipInfo;
	private TextView frequencyView;
	private TextView networkNameView;
	private TextView protectionView;
	private TextView passwordView;
	private TextView ipView;

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static InfoFragment newInstance(int sectionNumber) {
		InfoFragment fragment = new InfoFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public InfoFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_adhoc, container, false);
		setupUI(rootView);
		activity.setInfoFragmentTAG(getTag());
		Bundle arguments = getArguments() != null ? getArguments() : savedInstanceState;
		if (arguments != null) {
			setIP(activity.getIP());
			setNetwork(activity.getNetwork());

		}
		return rootView;
	}

	private void setupUI(View rootView) {
		networkNameView = ((TextView) rootView.findViewById(R.id.txt_Network_Name));
		frequencyView = ((TextView) rootView.findViewById(R.id.txt_Frequency));
		protectionView = ((TextView) rootView.findViewById(R.id.txt_Protection));
		passwordView = ((TextView) rootView.findViewById(R.id.txt_Password));
		ipView = ((TextView) rootView.findViewById(R.id.txt_IP));
		activity = (Adhoc) getActivity();
	}

	public void refreshNetworkUI() {
		networkNameView.setText(network.getNetworkName());
		frequencyView.setText(String.valueOf(network.getFrequency()));
		protectionView.setText(network.useWEP() ? R.string.txt_protection_wep : R.string.txt_protection_none);
		passwordView.setText(network.useWEP() ? network.getWepKey() : "None");
	}

	public void setNetwork(Network network) {
		this.network = network;
		refreshNetworkUI();
	}

	public void setIP(IPInfo ipInfo) {
		this.ipInfo = ipInfo;
		if (ipInfo != null) {
			ipView.setText(ipInfo.getIpAddress());
		}
	}

}
