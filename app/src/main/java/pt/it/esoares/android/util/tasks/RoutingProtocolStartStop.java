package pt.it.esoares.android.util.tasks;

import android.os.AsyncTask;

import java.io.File;

import eu.chainfire.libsuperuser.Shell;
import pt.it.esoares.android.routing.RoutingProtocolsContent;
import pt.it.esoares.android.util.GenericExecutionCallback;

public class RoutingProtocolStartStop {

	public static void restartRoutingProtocols(String... routingProtocols) {
		new AsyncTask<String, Void, Void>() {
			@Override
			protected Void doInBackground(String... params) {
				if (params == null) {
					return null;
				}
				for (int i = 0; i < params.length; i++) {
					String p = RoutingProtocolsContent.PROTOCOLS_LOCATION + File.separator + params[i] + File.separator;
					Shell.SU.run(p + "stop.sh");
					Shell.SU.run(p + "start.sh");
				}
				return null;
			}
		}.execute(routingProtocols);
	}

	public static void startRoutingProtocols(final GenericExecutionCallback listener, String... routingProtocols) {
		new AsyncTask<String, Void, Void>() {
			@Override
			protected Void doInBackground(String... params) {
				if (params == null) {
					return null;
				}
				for (int i = 0; i < params.length; i++) {
					String p = RoutingProtocolsContent.PROTOCOLS_LOCATION + File.separator + params[i] + File.separator;
					Shell.SU.run(p + "start.sh");
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				super.onPostExecute(aVoid);
				if (listener != null) {
					listener.onSuccessfulExecution();
				}
			}
		}.execute(routingProtocols);
	}

	public static void stopRoutingProtocols(final GenericExecutionCallback listener, String... routingProtocols) {
		new AsyncTask<String, Void, Void>() {
			@Override
			protected Void doInBackground(String... params) {
				if (params == null) {
					return null;
				}
				for (int i = 0; i < params.length; i++) {
					String p = RoutingProtocolsContent.PROTOCOLS_LOCATION + File.separator + params[i] + File.separator;
					Shell.SU.run(p + "stop.sh");
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				super.onPostExecute(aVoid);
				if (listener != null) {
					listener.onSuccessfulExecution();
				}
			}
		}.execute(routingProtocols);
	}

}
