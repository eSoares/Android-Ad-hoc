package pt.it.esoares.android.wpa_supplicant;

import android.content.res.Resources;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;

import pt.it.esoares.android.util.FileCopyFromResources;

public class WpaCliDeployer
		extends
		AsyncTask<pt.it.esoares.android.wpa_supplicant.WpaCliDeployer.Data, Integer, pt.it.esoares.android.wpa_supplicant.WpaCliDeployer.Result> {
	public static final int STATUS_CODE_NOT_EXPECIFIED = 0;
	private WpaCliDeployListener listener;

	public WpaCliDeployer(WpaCliDeployListener listeneer) {
		super();
		this.listener = listeneer;
	}

	@Override
	protected Result doInBackground(Data... params) {
		if (params.length < 1) {
			return new Result(new WpaCliDeployException("Wrong parameter size"));
		}
		Data param = params[0];
		// first we place the OLSR
		boolean result = false;
		try {
			result = FileCopyFromResources.copy(new File(param.getDestination()),
					param.getResource().openRawResource(param.getID()));
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

	
	public void execute(Resources resources, int resourceID, String privateAppFolderLocation) {
		Data raw = new Data(resources, resourceID, privateAppFolderLocation);
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

	class Data {
		private Resources resource;
		private int ID;
		private String destination;

		public Resources getResource() {
			return resource;
		}

		public int getID() {
			return ID;
		}

		public String getDestination() {
			return destination;
		}

		public Data(Resources resource, int iD, String destination) {
			super();
			this.resource = resource;
			ID = iD;
			this.destination = destination;
		}

	}
}
