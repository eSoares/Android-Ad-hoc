package pt.it.esoares.android.olsr;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import pt.it.esoares.android.util.GenericExecutionCallback;

public class OLSRConfigDeploy extends AsyncTask<String, Void, Boolean> {

	private GenericExecutionCallback callback;

	@Override
	protected Boolean doInBackground(String... arg0) {
		if (arg0.length < 2) {
			return false;
		}
		String olsrConfigPath = arg0[0];
		String olsrConfig = arg0[1];
		File f = new File(olsrConfigPath);
		try {
			FileWriter writer = new FileWriter(f);
			writer.append(olsrConfig);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			callback.onSucessfullExecution();
		} else {
			callback.onUnsucessfullExecution();
		}
		super.onPostExecute(result);
	}

	public void execute(String olsrConfigPath, String config, GenericExecutionCallback callback) {
		this.callback = callback;
		String[] coisas = { olsrConfigPath, config };
		this.execute(coisas);
	}

}
