package pt.it.esoares.android.ui.tools;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;
import pt.it.esoares.android.olsrdeployer.R;

public class PingDialog extends DialogFragment {

	private AlertDialog dialog;

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Ping");
		builder.setView(R.layout.dialog_ping);
		dialog = builder.create();
		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface d) {
				dialog.findViewById(R.id.bt_start).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						ping(((TextView) dialog.findViewById(R.id.txt_IP)).getText().toString());
					}
				});

			}
		});

		return dialog;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private void ping(String ip) {
		new Ping().execute(ip);
	}


	@Override
	public void onDetach() {
		super.onDetach();
//		if (loadNodes != null) {
//			loadNodes.cancel(true);
//			loadNodes = null;
//		}
	}

	class Ping extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			List<String> output = Shell.SU.run(String.format("ping -c 4 %s", params[0]));
			StringBuilder result = new StringBuilder();
			for (int i = 0; i < output.size(); i++) {
				result.append(output.get(i));
				result.append("\n");
			}
			return result.toString();
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			((TextView) dialog.findViewById(R.id.txt_output)).setText(s);
		}
	}
}
