package pt.it.esoares.android.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.EditText;

import pt.it.esoares.android.olsrdeployer.R;

public class GpsSettingsDialog extends DialogFragment {
	public static final String GPS_LOG_FILE = "gps_log_file";
	public static final String GPS_DEFAULT_LOG_FILE = "gps_log.txt";
	static private AlertDialog dialog;
	private String log_file;

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(R.layout.dialog_gps_settings);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface d, int which) {
				log_file = ((EditText) dialog.findViewById(R.id.txt_log_file)).getText().toString();
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
				SharedPreferences.Editor editor = prefs.edit().putString(GPS_LOG_FILE, log_file);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
					editor.apply();
				} else {
					editor.commit();
				}
			}
		});
		dialog = builder.create();
		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface d) {
				EditText txt = ((EditText) dialog.findViewById(R.id.txt_log_file));
				if (txt != null) {
					if (txt.getText().toString().length() == 0) {
						txt.setText(log_file);
					}
				}
			}
		});

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		log_file = prefs.getString(GPS_LOG_FILE, GPS_DEFAULT_LOG_FILE);

		return dialog;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d("gps", "onAttach: ");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		log_file = ((EditText) dialog.findViewById(R.id.txt_log_file)).getText().toString();
	}

}
