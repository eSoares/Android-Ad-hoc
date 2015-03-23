package pt.it.esoares.android.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import pt.it.esoares.android.devices.Network;
import pt.it.esoares.android.olsrdeployer.R;
import pt.it.esoares.android.wpa_supplicant.ScanExistingNetworks;
import pt.it.esoares.android.wpa_supplicant.ScanNetworkListener;
import pt.it.esoares.android.wpa_supplicant.ScanNetworksException;

/**
 * Fragment to search networks
 */
public class SearchNetworksFragment extends ListFragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	private static ArrayAdapter<Network> adapter;

	private MenuItem refresh;

	private Adhoc activity;

	private ArrayList<Network> networksArround = new ArrayList<>();

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static SearchNetworksFragment newInstance(int sectionNumber) {
		SearchNetworksFragment fragment = new SearchNetworksFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.search_networks, menu);
		refresh = menu.findItem(R.id.menu_search_progress);
		MenuItemCompat.setShowAsAction(refresh, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		scanNetworks();
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_search_progress) {
			scanNetworks();
		}
		return super.onOptionsItemSelected(item);
	}

	public SearchNetworksFragment() {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		activity = (Adhoc) getActivity();
		adapter = new ArrayAdapter<Network>(getActivity(), android.R.layout.simple_list_item_1, networksArround);
		setListAdapter(adapter);
	}

	public void addNetwork(Network network) {
		if (!networksArround.contains(network)) {
			adapter.add(network);
			adapter.notifyDataSetChanged();
		}
	}

	public void scanNetworksFinish() {
		if (refresh != null) {
			MenuItemCompat.getActionView(refresh).clearAnimation();
			MenuItemCompat.collapseActionView(refresh);
			MenuItemCompat.setActionView(refresh, null);
		}
	}

	public void scanNetworksStarted() {
		if (refresh != null) {
			LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ImageView iv = (ImageView) inflater.inflate(R.layout.action_progressbar, null);

			Animation rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.refresh);
			rotation.setRepeatCount(Animation.INFINITE);
			iv.startAnimation(rotation);

			MenuItemCompat.setActionView(refresh, iv);
			MenuItemCompat.expandActionView(refresh);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		final int pox = position;
		OnClickListener resultListener = new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == AlertDialog.BUTTON_POSITIVE) {
					activity.changeNetwork(networksArround.get(pox));
					activity.mViewPager.setCurrentItem(0);
				}
			}
		};
		Builder dialog = new AlertDialog.Builder(getActivity()).setTitle("Connecto to this network?")
				.setNegativeButton("No", resultListener).setPositiveButton("Yes", resultListener);
		dialog.create().show();
		super.onListItemClick(l, v, position, id);
	}

	void scanNetworks() {
		scanNetworksStarted();
		new ScanExistingNetworks(new ScanNetworkListener() {

			@Override
			public void onNetworkFound(Network network) {
				addNetwork(network);
			}

			@Override
			public void onEndedWithError(ScanNetworksException scanNetworksException) {
				scanNetworksFinish();
			}

			@Override
			public void onEnd() {
				scanNetworksFinish();

			}
		}).run();
		;
	}

}