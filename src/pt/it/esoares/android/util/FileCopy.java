package pt.it.esoares.android.util;

import android.os.AsyncTask;

import eu.chainfire.libsuperuser.Shell.SU;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class FileCopy extends AsyncTask<CopyFromRawArg, Integer, Boolean> {

	@Override
	protected Boolean doInBackground(CopyFromRawArg... arg0) {
		if (arg0.length != 1) {
			return false;
		}
		File destination = new File(arg0[0].getDestination());
		if (destination.exists()) {
			return false;
		}
		try {
			return FileCopy.copy(destination, arg0[0].getResource().openRawResource(arg0[0].getID()));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	abstract protected void onPostExecute(Boolean result);

	@Override
	abstract protected void onProgressUpdate(Integer... values);

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

}
