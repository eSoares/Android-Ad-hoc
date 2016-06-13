package pt.it.esoares.android.ui;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import pt.it.esoares.android.olsrdeployer.R;
import pt.it.esoares.android.olsrdeployer.databinding.FragmentRoutingprotocolBinding;
import pt.it.esoares.android.routing.RoutingProtocol;

/**
 * {@link RecyclerView.Adapter} that can display a {@link RoutingProtocol}
 */
public class MyRoutingProtocolRecyclerViewAdapter extends RecyclerView.Adapter<MyRoutingProtocolRecyclerViewAdapter.ViewHolder> {

	private final List<RoutingProtocol> mValues;

	public MyRoutingProtocolRecyclerViewAdapter(List<RoutingProtocol> items) {
		mValues = items;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		FragmentRoutingprotocolBinding binding = DataBindingUtil
				.inflate(LayoutInflater.from(parent.getContext()), R.layout.fragment_routingprotocol, parent, false);
		return new ViewHolder(binding);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.mItem = mValues.get(position);
		holder.position = position;
		holder.binding.setProtocol(holder.mItem);
	}

	@Override
	public int getItemCount() {
		return mValues.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public final View mView;
		public final FragmentRoutingprotocolBinding binding;
		public int position;
		public RoutingProtocol mItem;

		public ViewHolder(FragmentRoutingprotocolBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
			mView = binding.getRoot();
		}
	}
}
