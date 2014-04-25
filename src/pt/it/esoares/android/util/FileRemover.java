package pt.it.esoares.android.util;

import android.os.AsyncTask;

import eu.chainfire.libsuperuser.Shell.SU;

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
			listener.onSucessfullExecution();
		} else {
			listener.onUnsucessfullExecution();
		}
	}

	public static void removeFile(String filePath) {
		SU.run("rm " + filePath);
	}
}
