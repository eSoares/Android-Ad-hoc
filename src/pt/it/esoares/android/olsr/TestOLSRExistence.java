package pt.it.esoares.android.olsr;

import android.os.AsyncTask;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public abstract class TestOLSRExistence extends AsyncTask<String, Void, Boolean> {

	@Override
	protected Boolean doInBackground(String... arg0) {
		if (!Shell.SU.available()) {
			return false;
		}
		if (check("olsrd")) {
			return true;
		}
		if (arg0.length < 1) {
			return false;
		}
		for (String location : arg0) {
			if (check(location)) {
				return true;
			}
		}
		return false;

	}

	private boolean check(String command) {
		List<String> result = Shell.SU.run(command);
		if (result.size() > 3) {
			return true;
		}
		return false;
	}

	@Override
	abstract protected void onPostExecute(Boolean result);
}
