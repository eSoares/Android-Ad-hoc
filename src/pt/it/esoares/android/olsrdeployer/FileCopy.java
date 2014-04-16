package pt.it.esoares.android.olsrdeployer;

import android.os.AsyncTask;

import eu.chainfire.libsuperuser.Shell.SU;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
			if (!destination.createNewFile()) {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		InputStream instream = arg0[0].getResource().openRawResource(arg0[0].getID());
		byte[] buffer = new byte[512];
		int size;
		try {
			OutputStream outStream = new FileOutputStream(destination);
			size = instream.read(buffer);
			while (size > 0) {
				outStream.write(buffer, 0, size);
				size = instream.read(buffer);
			}
			outStream.close();
			instream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		SU.run("chmod 755 "+arg0[0].getDestination());
		return true;
	}

	@Override
	abstract protected void onPostExecute(Boolean result);

	@Override
	abstract protected void onProgressUpdate(Integer... values);

}
