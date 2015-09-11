package pt.it.esoares.android.util.tasks;

import android.content.Context;
import android.os.AsyncTask;

import pt.it.esoares.android.devices.Device;
import pt.it.esoares.android.devices.DeviceFactory;
import pt.it.esoares.android.olsr.ExecuteOLSR;
import pt.it.esoares.android.olsr.OLSRConfigDeploy;
import pt.it.esoares.android.olsr.OLSRGenerator;
import pt.it.esoares.android.olsr.OLSRSetting;
import pt.it.esoares.android.util.GenericExecutionCallback;

public class StartOLSR {
	private String olsrConfigPath;
	private String olsrExecutionPath;
	private GenericExecutionCallback callback;

	public StartOLSR(String olsrConfigPath, String olsrExecutionPath, GenericExecutionCallback callback) {
		super();
		this.olsrConfigPath = olsrConfigPath;
		this.olsrExecutionPath = olsrExecutionPath;
		this.callback = callback;
	}

	private void setupOLSR(Data d) {
		new AsyncTask<Data, Void, String>() {

			@Override
			protected String doInBackground(Data... params) {
				Device device = DeviceFactory.getDevice(params[0].context);
				return OLSRGenerator.getOLSRConfig(device, params.length != 0 ? new OLSRSetting() : params[0].setting);
			}

			protected void onPostExecute(String result) {
				// deploy olsrConfig
				new OLSRConfigDeploy().execute(olsrConfigPath, result, new GenericExecutionCallback() {

					@Override
					public void onUnsucessfullExecution() {
						callback.onUnsucessfullExecution();
					}

					@Override
					public void onSucessfullExecution() {
						// Execute OLSR
						new ExecuteOLSR().execute(olsrExecutionPath, olsrConfigPath, new GenericExecutionCallback() {

							@Override
							public void onUnsucessfullExecution() {
								callback.onUnsucessfullExecution();
							}

							@Override
							public void onSucessfullExecution() {
								callback.onSucessfullExecution();
							}
						});
					}
				});

			};

		}.execute(d);
	}

	public void startOlsr(Context context, OLSRSetting setting) {
		setupOLSR(new Data(context, setting));
	}

	class Data {
		Context context;
		OLSRSetting setting;

		public Data(Context context, OLSRSetting setting) {
			super();
			this.context = context;
			this.setting = setting;
		}

	}
}
