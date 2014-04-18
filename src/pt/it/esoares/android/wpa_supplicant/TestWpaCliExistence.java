package pt.it.esoares.android.wpa_supplicant;

import android.os.AsyncTask;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public abstract class TestWpaCliExistence extends AsyncTask<String, Void, Boolean> {

	@Override
	protected Boolean doInBackground(String... arg0) {
		if (!Shell.SU.available()) {
			return false;
		}
		if (check("wpa_cli"/* quit"*/)) {
			return true;
		}
		if (arg0.length < 1) {
			return false;
		}
		for (String location : arg0) {
			if (check(location/*+" quit"*/)) { 
			return true;
			}
		}
		return false;

	}

	private boolean check(String command) {
		String[] s={command, "quit"};
		List<String> result = Shell.SU.run(s);
		if(result==null){
			return false;
		}
		if (result.size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	abstract protected void onPostExecute(Boolean result);
}
