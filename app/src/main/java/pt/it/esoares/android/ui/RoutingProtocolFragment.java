package pt.it.esoares.android.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pt.it.esoares.android.olsrdeployer.R;
import pt.it.esoares.android.routing.RoutingProtocolsContent;
import pt.it.esoares.android.routing.StatusStorage;

/**
 * A fragment representing a list of Items.
 */
public class RoutingProtocolFragment extends Fragment {

	private RecyclerView recyclerView;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public RoutingProtocolFragment() {
	}

	@SuppressWarnings("unused")
	public static RoutingProtocolFragment newInstance() {
		return new RoutingProtocolFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_routingprotocol_list, container, false);
		recyclerView = (RecyclerView) view.findViewById(R.id.list);
		// Set the adapter
		if (recyclerView != null) {
			Context context = view.getContext();
			recyclerView.setLayoutManager(new LinearLayoutManager(context));
			recyclerView.setAdapter(new MyRoutingProtocolRecyclerViewAdapter(RoutingProtocolsContent.ITEMS));
		}
		refreshRoutingProtocols();
		StatusStorage.loadProtocolsStatus(getContext());

		return view;
	}


	@Override
	public void onDetach() {
		super.onDetach();
		StatusStorage.saveProtocolsStatus(getContext());
	}

	private void refreshRoutingProtocols() {
		RoutingProtocolsContent.reload(getContext(), new RoutingProtocolsContent.OnRoutingProtocolsUpdated() {
			@Override
			public void OnRoutingProtocolsUpdated() {
				recyclerView.getAdapter().notifyDataSetChanged();
				StatusStorage.loadProtocolsStatus(getContext());

			}
		});
	}
}
