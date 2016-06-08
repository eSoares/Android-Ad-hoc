package pt.it.esoares.android.util.tasks;

import android.os.AsyncTask;

import eu.chainfire.libsuperuser.Shell.SU;
import pt.it.esoares.android.util.GenericExecutionCallback;

public class FileRemover extends AsyncTask<String, Void, Boolean> {

	private GenericExecutionCallback listener;

	@Override
	protected Boolean doInBackground(String... params) {
		if (params.length < 1) {
			return false;
		}
		for (String file : params) {
			removeFile(file);
		}
		return true;
	}

	public void execute(String[] filesPathToRemove, GenericExecutionCallback callback) {
		this.listener = callback;
		this.execute(filesPathToRemove);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			listener.onSuccessfulExecution();
		} else {
			listener.onUnsuccessfulExecution();
		}
	}

	public static void removeFile(String filePath) {
		SU.run("rm " + filePath);
	}
}
