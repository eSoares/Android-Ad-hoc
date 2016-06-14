package pt.it.esoares.android.util.tasks;

import android.os.AsyncTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;
import pt.it.esoares.android.routing.RoutingProtocolsContent;
import pt.it.esoares.android.util.GenericExecutionCallback;

public class RoutingProtocolStartStop {

	private final static String FORMAT_ENVIRONMENT_VARIABLES = "export %s=\"%s\"";

	public static void restartRoutingProtocols(String... routingProtocols) {
		new AsyncTask<String, Void, Void>() {
			@Override
			protected Void doInBackground(String... params) {
				if (params == null) {
					return null;
				}
				for (int i = 0; i < params.length; i++) {
					String p = RoutingProtocolsContent.PROTOCOLS_LOCATION + File.separator + params[i] + File.separator;
					List<String> commands = getExportedEnvironmentVariables();
					commands.add(p + "stop.sh");
					Shell.SU.run(commands);
					commands.remove(commands.size() - 1);
					commands.add(p + "start.sh");
					Shell.SU.run(commands);
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
					List<String> commands = getExportedEnvironmentVariables();
					commands.add(p + "start.sh");
					Shell.SU.run(commands);
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
					List<String> commands = getExportedEnvironmentVariables();
					commands.add(p + "stop.sh");
					Shell.SU.run(commands);
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

	private static List<String> getExportedEnvironmentVariables() {
		List<String> result = new ArrayList<>(10);
		result.add(String.format(FORMAT_ENVIRONMENT_VARIABLES, "iwface", "wlan0")); // todo fix to be dynamic


		return result;
	}
}
