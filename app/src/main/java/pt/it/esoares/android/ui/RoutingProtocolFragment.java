package pt.it.esoares.android.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

import pt.it.esoares.android.olsrdeployer.R;
import pt.it.esoares.android.routing.RoutingProtocolsContent;
import pt.it.esoares.android.routing.RoutingProtocolsContent.RoutingProtocol;
import pt.it.esoares.android.routing.StatusStorage;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class RoutingProtocolFragment extends Fragment {

	// TODO: Customize parameter argument names
	private static final String ARG_COLUMN_COUNT = "column-count";
	RecyclerView recyclerView;
	// TODO: Customize parameters
	private OnListFragmentInteractionListener mListener;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public RoutingProtocolFragment() {
	}

	// TODO: Customize parameter initialization
	@SuppressWarnings("unused")
	public static RoutingProtocolFragment newInstance(int columnCount) {
		RoutingProtocolFragment fragment = new RoutingProtocolFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_COLUMN_COUNT, columnCount);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
//			 getArguments().getInt(ARG_COLUMN_COUNT);
		}
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
			recyclerView.setAdapter(new MyRoutingProtocolRecyclerViewAdapter(RoutingProtocolsContent.ITEMS, mListener));
		}
		refreshRoutingProtocols();
		StatusStorage.loadProtocolsStatus(getContext());

		return view;
	}


	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnListFragmentInteractionListener) {
			mListener = (OnListFragmentInteractionListener) context;
		} else {
//			throw new RuntimeException(context.toString()
//					+ " must implement OnListFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
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

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p/>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnListFragmentInteractionListener {
		// TODO: Update argument type and name
		void onListFragmentInteraction(RoutingProtocol item);
	}
}
