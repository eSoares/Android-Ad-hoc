package pt.it.esoares.android.ui.tools;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;
import pt.it.esoares.android.olsrdeployer.R;
import pt.it.esoares.android.olsrdeployer.databinding.DialogKnownNodesBinding;

public class KnownNodesDialog extends DialogFragment {

	private AlertDialog dialog;
	private MyAdapter adapter;
	private LoadNodes loadNodes;

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
//		builder.setView(inflater.inflate(R.layout.dialog_known_nodes, null));
		adapter = new MyAdapter(getContext());
		builder.setAdapter(adapter, null);
//				new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog_, int which) {
//
//			}
//		});
		builder.setTitle("Known nodes");
		adapter.add(new Node("192.168.1.0", 1, 1, "line"));
		dialog = builder.create();
//		adapter.notifyDataSetChanged();
		dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d("click", String.format("onClick: %s", ((Node) dialog.getListView().getAdapter().getItem(position)).line));

			}
		});
		return dialog;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (loadNodes == null) {
			loadNodes = new LoadNodes();
		}
		loadNodes.execute();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		if (loadNodes != null) {
			loadNodes.cancel(true);
		}
	}

	class MyAdapter extends ArrayAdapter<Node> {
		List<Node> nodes = new ArrayList<>(3);
		private LayoutInflater inflater;

		public MyAdapter(Context context) {
			super(context, R.layout.dialog_known_nodes);
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return nodes.size();
		}

		@Override
		public Node getItem(int position) {
			return nodes.get(position);
		}

		@Override
		public void add(Node object) {
			if (!nodes.contains(object)) {
				nodes.add(object);
				notifyDataSetChanged();
			}
		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			DialogKnownNodesBinding binding;
			if (convertView == null) {
				binding = DataBindingUtil
						.inflate(inflater, R.layout.dialog_known_nodes, parent, false);
				binding.getRoot().setTag(binding);
				view = binding.getRoot();
			} else {
				binding = (DialogKnownNodesBinding) convertView.getTag();
				view = convertView;
			}
			binding.setNode(getItem(position));

			return view;
		}
	}

	public class Node {
		public String ip;
		public int hopCount;
		public int metric;
		public String line;

		public Node(String ip, int hopCount, int metric, String line) {
			this.ip = ip;
			this.hopCount = hopCount;
			this.metric = metric;
			this.line = line;
		}

		public Node(String line) {
			this.line = line;
			//todo parse line
			// line sample:
			// 0.0.0.0         169.254.0.1     0.0.0.0         UG    0      0        0 wlan0

		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			Node node = (Node) o;
			if (hopCount != node.hopCount) {
				return false;
			}
			if (metric != node.metric) {
				return false;
			}
			return ip != null ? ip.equals(node.ip) : node.ip == null;
		}

		@Override
		public int hashCode() {
			int result = ip != null ? ip.hashCode() : 0;
			result = 31 * result + hopCount;
			result = 31 * result + metric;
			result = 31 * result + (line != null ? line.hashCode() : 0);
			return result;
		}
	}

	class LoadNodes extends AsyncTask<Void, Node, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			List<String> output;
			while (true) {
				output = Shell.SU.run("busybox route -n");
				if (output != null) {
					for (int i = 2; i < output.size(); i++) { // ignore first line, they are headers
						if (!output.get(i).startsWith("0.0.0.0")) { // ignore showing default route
							publishProgress(new Node(output.get(i)));
						}
					}
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
//					e.printStackTrace();
				}
			}
//			return null;
		}

		@Override
		protected void onProgressUpdate(Node... values) {
			if (values == null) {
				return;
			}
			for (int i = 0; i < values.length; i++) {
				adapter.add(values[i]);
			}
			if (values.length > 1) {
				adapter.notifyDataSetChanged();
			}
			super.onProgressUpdate(values);
		}
	}
}
