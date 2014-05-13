package pt.it.esoares.android.olsr;

import android.os.AsyncTask;

import pt.it.esoares.android.util.GenericExecutionCallback;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.chainfire.libsuperuser.Shell.SU;

public class OLSRKiller extends AsyncTask<Void, Void, Boolean> {

	private final static Pattern regex = Pattern.compile("\\w+\\s+(\\d+)\\s+.*");
	private GenericExecutionCallback listener;

	@Override
	protected Boolean doInBackground(Void... arg0) {
		int id = getProcessID("olsrd");
		SU.run("kill -9 " + String.valueOf(id));
		if (getProcessID("/olsrd") != 0) {
			return false;
		} else {
			return true;
		}
	}

	private static int getProcessID(String string) {
		List<String> results = SU.run("ps");
		for (String res : results) {
			if (res.contains(string)) {
				Matcher matcher = regex.matcher(res);
				if (matcher.matches()) {
					return Integer.valueOf(matcher.group(1));

				}
			}
		}
		return 0;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			listener.onSucessfullExecution();
		} else {
			listener.onUnsucessfullExecution();
		}
		super.onPostExecute(result);
	}

	public void execute(GenericExecutionCallback callback) {
		this.listener = callback;
		this.execute();
	}

}
