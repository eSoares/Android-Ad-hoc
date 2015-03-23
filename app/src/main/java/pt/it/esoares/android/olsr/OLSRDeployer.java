package pt.it.esoares.android.olsr;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import pt.it.esoares.android.devices.DeviceFactory;
import pt.it.esoares.android.olsrdeployer.R;
import pt.it.esoares.android.util.FileCopyFromResources;

public class OLSRDeployer
		extends
		AsyncTask<pt.it.esoares.android.olsr.OLSRDeployer.Data, Integer, pt.it.esoares.android.olsr.OLSRDeployer.Result> {
	public static final int STATUS_CODE_NOT_EXPECIFIED = 0;
	private OLSRDeployListener listener;

	public OLSRDeployer(OLSRDeployListener listeneer) {
		super();
		this.listener = listeneer;
	}

	@Override
	protected Result doInBackground(Data... params) {
		if (params.length < 1) {
			return new Result(new OLSRDeployException("Wrong parameter size"));
		}
		Data olsr = params[0];
		// first we place the OLSR
		boolean result;
		try {
			result = FileCopyFromResources.copy(new File(olsr.getDestination() + "olsr"), olsr.getResource()
					.openRawResource(R.raw.olsrd));
		} catch (IOException e1) {
			return new Result(new OLSRDeployException(e1.toString()));
		}
		if (!result) {
			return new Result(new OLSRDeployException("Deploy of OLSR failed"));
		}
		// TODO: test the OLSR installation
		// then the olsr config
		String olsr_config = OLSRGenerator.getOLSRConfig(DeviceFactory.getDevice(params[0].getContext()),
				new OLSRSetting());
		try {
			deployOLSRConfig(olsr.getDestination(), olsr_config);
		} catch (IOException e) {
			return new Result(new OLSRDeployException(e.toString()));
		}
		return new Result();
	}

	private static boolean deployOLSRConfig(String destination, String config) throws IOException {
		File f = new File(destination + "olsr.conf");
		if (!f.createNewFile()) {
			return false;
		}
		FileWriter writer = new FileWriter(f);
		writer.append(config);
		writer.flush();
		writer.close();
		return true;

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

	public void run(Resources resources, String privateAppFolderLocation, Context context) {
		Data raw = new Data(resources, R.raw.olsrd, privateAppFolderLocation, context);
		this.execute(raw);
	}

	class Result {
		private OLSRDeployException exception;

		Result() {
		}

		Result(OLSRDeployException exception) {
			this.exception = exception;
		}

		public boolean failed() {
			return exception != null;
		}

		public OLSRDeployException getException() {
			return exception;
		}
	}

	class Data {
		private Resources resource;
		private int ID;
		private String destination;
		private Context context;

		public Resources getResource() {
			return resource;
		}

		public Context getContext() {
			return context;
		}

		public int getID() {
			return ID;
		}

		public String getDestination() {
			return destination;
		}

		public Data(Resources resource, int iD, String destination, Context context) {
			super();
			this.resource = resource;
			this.ID = iD;
			this.destination = destination;
			this.context = context;
		}

	}
}
