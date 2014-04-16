package pt.it.esoares.android.util;

import android.os.AsyncTask;

import eu.chainfire.libsuperuser.Shell.SU;

public abstract class FileRemover extends AsyncTask<String, Void, Boolean> {

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

	@Override
	protected abstract void onPostExecute(Boolean result);

	public static void removeFile(String filePath){
		SU.run("rm " + filePath);
	}
}
