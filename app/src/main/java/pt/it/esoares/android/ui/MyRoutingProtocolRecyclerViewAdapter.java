package pt.it.esoares.android.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import pt.it.esoares.android.olsrdeployer.R;
import pt.it.esoares.android.ui.RoutingProtocolFragment.OnListFragmentInteractionListener;
import pt.it.esoares.android.routing.RoutingProtocolsContent.RoutingProtocol;

/**
 * {@link RecyclerView.Adapter} that can display a {@link RoutingProtocol} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyRoutingProtocolRecyclerViewAdapter extends RecyclerView.Adapter<MyRoutingProtocolRecyclerViewAdapter.ViewHolder> {

	private final List<RoutingProtocol> mValues;
	private final OnListFragmentInteractionListener mListener;

	public MyRoutingProtocolRecyclerViewAdapter(List<RoutingProtocol> items, OnListFragmentInteractionListener listener) {
		mValues = items;
		mListener = listener;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.fragment_routingprotocol, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		holder.mItem = mValues.get(position);
		holder.position=position;
//		holder.mIdView.setText(mValues.get(position).id);
		holder.start.setText(mValues.get(position).isRunning ? R.string.button_stop_state : R.string.button_start_state);
		holder.mContentView.setText(mValues.get(position).name);
		holder.settings.setEnabled(holder.mItem.hasSettings);

		holder.start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != mListener) {
					// Notify the active callbacks interface (the activity, if the
					// fragment is attached to one) that an item has been selected.
					mListener.onListFragmentInteraction(holder.mItem);
				}
				holder.mItem.isRunning = !holder.mItem.isRunning;
				notifyItemChanged(holder.position);
			}
		});
	}

	@Override
	public int getItemCount() {
		return mValues.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public int position;
		public final View mView;
		public final TextView mContentView;
		public final Button settings;
		public final Button start;
		public RoutingProtocol mItem;

		public ViewHolder(View view) {
			super(view);
			mView = view;
			mContentView = (TextView) view.findViewById(R.id.name);
			settings = (Button) view.findViewById(R.id.settings);
			start = (Button) view.findViewById(R.id.start_stop_button);
		}

		@Override
		public String toString() {
			return super.toString() + " '" + mContentView.getText() + "'";
		}
	}
}
