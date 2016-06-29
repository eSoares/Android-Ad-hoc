package pt.it.esoares.adhocdroid.ui.tools;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;
import pt.it.esoares.adhocdroid.R;

public class ToolDialog extends DialogFragment {
	static private AlertDialog dialog;
	static private Tool tool;
	private int string_title;
	private int string_button_name;
	private String toolString;

	protected void setup(int string_title, int string_button_name, String toolString) {
		this.string_title = string_title;
		this.string_button_name = string_button_name;
		this.toolString = toolString;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(string_title);
		builder.setView(R.layout.dialog_tool);
		dialog = builder.create();
		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface d) {

				View button = dialog.findViewById(R.id.bt_start);
				if (button != null) {
					((Button) button).setText(string_button_name);
					button.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							runTool(getIp());
						}
					});
				}
				ProgressBar pb = ((ProgressBar) dialog.findViewById(R.id.progressBar));
				if (pb != null) {
					pb.setVisibility(View.GONE);
				}
				dialog.findViewById(R.id.txt_output).setVisibility(View.GONE);
				dialog.findViewById(R.id.txt_output_label).setVisibility(View.GONE);
			}
		});

		return dialog;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		if(tool!=null) {
			tool.cancel(true);
		}
		tool = null;
	}

	private void runTool(String ip) {
		if (tool == null) {
			tool = new Tool(toolString);
			tool.execute(ip);
			ProgressBar pb = ((ProgressBar) dialog.findViewById(R.id.progressBar));
			if (pb != null) {
				pb.setVisibility(View.VISIBLE);
				pb.setIndeterminate(true);
			}
			((Button) dialog.findViewById(R.id.bt_start)).setText(R.string.button_stop_state);
		} else {
			tool.cancel(true);
			tool = null;
			dialog.findViewById(R.id.progressBar).setVisibility(View.GONE);
			((Button) dialog.findViewById(R.id.bt_start)).setText(string_button_name);
		}
		dialog.findViewById(R.id.txt_output).setVisibility(View.GONE);
		dialog.findViewById(R.id.txt_output_label).setVisibility(View.GONE);
	}

	protected String getIp() {
		return ((TextView) dialog.findViewById(R.id.txt_IP)).getText().toString();
	}

	protected void postResults(String s) {
		((TextView) dialog.findViewById(R.id.txt_output)).setText(s);
	}

	class Tool extends AsyncTask<String, Void, String> {

		String toolString = "ping -c 4 %s";

		public Tool(String toolString) {
			this.toolString = toolString;
		}

		@Override
		protected String doInBackground(String... params) {
			List<String> output = Shell.SU.run(String.format(toolString, params[0]));
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
			postResults(s);
			ProgressBar pb = ((ProgressBar) dialog.findViewById(R.id.progressBar));
			if (pb != null) {
				pb.setVisibility(View.GONE);
			}
			dialog.findViewById(R.id.txt_output).setVisibility(View.VISIBLE);
			dialog.findViewById(R.id.txt_output_label).setVisibility(View.VISIBLE);
			((Button) dialog.findViewById(R.id.bt_start)).setText(string_button_name);
		}
	}

}
