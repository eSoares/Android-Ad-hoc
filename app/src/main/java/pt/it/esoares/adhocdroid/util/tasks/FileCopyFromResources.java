package pt.it.esoares.adhocdroid.util.tasks;

import android.content.res.Resources;
import android.os.AsyncTask;

import eu.chainfire.libsuperuser.Shell.SU;
import pt.it.esoares.adhocdroid.util.GenericExecutionCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileCopyFromResources extends
		AsyncTask<FileCopyFromResources.Data, Integer, Boolean> {

	private GenericExecutionCallback listener;

	@Override
	protected Boolean doInBackground(Data... arg0) {
		if (arg0.length != 1) {
			return false;
		}
		File destination = new File(arg0[0].getDestination());
		if (destination.exists()) {
			return false;
		}
		try {
			return FileCopyFromResources.copy(destination, arg0[0].getResource().openRawResource(arg0[0].getID()));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void execute(Resources resource, int resourceID, String destination, GenericExecutionCallback callback) {
		this.listener = callback;
		this.execute(new Data(resource, resourceID, destination));
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			listener.onSuccessfulExecution();
		} else {
			listener.onUnsuccessfulExecution();
		}
	}

	public static boolean copy(File destination, InputStream source) throws IOException {
		if (!destination.createNewFile()) {
			return false;
		}

		byte[] buffer = new byte[512];
		int size;
		OutputStream outStream = new FileOutputStream(destination);
		size = source.read(buffer);
		while (size > 0) {
			outStream.write(buffer, 0, size);
			size = source.read(buffer);
		}
		outStream.close();
		source.close();
		SU.run("chmod 755 " + destination.getAbsolutePath());
		// TODO check if chmod correctly was applied
		return true;
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
