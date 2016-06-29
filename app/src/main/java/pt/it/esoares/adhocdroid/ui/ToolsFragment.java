package pt.it.esoares.adhocdroid.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import pt.it.esoares.adhocdroid.R;
import pt.it.esoares.adhocdroid.ui.tools.KnownNodesDialog;
import pt.it.esoares.adhocdroid.ui.tools.PingDialog;
import pt.it.esoares.adhocdroid.ui.tools.TracerouteDialog;
import pt.it.esoares.adhocdroid.util.GpsLocationService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ToolsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ToolsFragment extends Fragment {

	public ToolsFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment ToolsFragment.
	 */
	public static ToolsFragment newInstance() {
		return new ToolsFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_tools, container, false);
		view.findViewById(R.id.getKnowNodes).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new KnownNodesDialog().show(getFragmentManager(), "knownodes");
			}
		});

		(view.findViewById(R.id.ping)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new PingDialog().show(getFragmentManager(), "ping");
			}
		});

		(view.findViewById(R.id.traceroute)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new TracerouteDialog().show(getFragmentManager(), "traceroute");
			}
		});


		if (GpsLocationService.isRunning) {
			((Button) (view.findViewById(R.id.start_stop_button_gps))).setText(R.string.button_stop_state);
		}
		(view.findViewById(R.id.gps_settings)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new GpsSettingsDialog().show(getFragmentManager(), "gps_settings");
			}
		});
		(view.findViewById(R.id.start_stop_button_gps)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), GpsLocationService.class);
				if (GpsLocationService.isRunning) {
					getContext().stopService(intent);
					((Button) v).setText(R.string.button_start_state);
				} else {
					intent.putExtra(GpsLocationService.EXTRA_START, true);
					getContext().startService(intent);
					((Button) v).setText(R.string.button_stop_state);
				}
			}
		});

		return view;
	}

}
