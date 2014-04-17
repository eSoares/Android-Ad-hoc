package pt.it.esoares.android.wpa_supplicant;

import android.content.res.Resources;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;

import pt.it.esoares.android.olsrdeployer.R;
import pt.it.esoares.android.util.CopyFromRawArg;
import pt.it.esoares.android.util.FileCopy;

public class WpaCliDeployer extends AsyncTask<CopyFromRawArg, Integer, pt.it.esoares.android.wpa_supplicant.WpaCliDeployer.Result> {
	public static final int STATUS_CODE_NOT_EXPECIFIED = 0;
	private WpaCliDeployListener listener;

	public WpaCliDeployer(WpaCliDeployListener listeneer) {
		super();
		this.listener = listeneer;
	}

	@Override
	protected Result doInBackground(CopyFromRawArg... params) {
		if (params.length < 1) {
			return new Result(new WpaCliDeployException("Wrong parameter size"));
		}
		CopyFromRawArg param = params[0];
		// first we place the OLSR
		boolean result = false;
		try {
			result = FileCopy.copy(new File(param.getDestination()),
					param.getResource().openRawResource(R.raw.wpa_cli));
		} catch (IOException e) {
			return new Result(new WpaCliDeployException(e.toString()));
		}
		if (!result) {
			return new Result(new WpaCliDeployException("Deploy of OLSR failed"));
		}
		// TODO: test the wpa_cli installation
		return new Result();
	}
	
	@Override
	protected void onPostExecute(Result result) {
		if (!result.failed()) {
			listener.onDeployFinish();
		} else {
			listener.onError(result.getException());
		}

	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		listener.onDeployStatusUpdate(values[0], values.length > 1 ? values[1] : STATUS_CODE_NOT_EXPECIFIED);
	}

	public void run(Resources resources, String privateAppFolderLocation) {
		CopyFromRawArg raw = new CopyFromRawArg(resources, R.raw.olsrd, privateAppFolderLocation);
		this.execute(raw);
	}

	class Result {
		private WpaCliDeployException exception;

		Result() {
		}

		Result(WpaCliDeployException exception) {
			this.exception = exception;
		}

		public boolean failed() {
			return exception != null;
		}

		public WpaCliDeployException getException() {
			return exception;
		}

	}
}
