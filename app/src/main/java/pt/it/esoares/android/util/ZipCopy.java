package pt.it.esoares.android.util;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import eu.chainfire.libsuperuser.Shell;

public class ZipCopy {
	private static final int BUFFER_SIZE = 1024 * 2;

	private ZipCopy() {
	}

	public static void copyFromZip(File zipFile, File destFolder) throws IOException {
		ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));

		//get the zipped file list entry
		ZipEntry ze = zis.getNextEntry();

		byte[] buffer = new byte[BUFFER_SIZE];
		try {
			while (ze != null) {

				String fileName = ze.getName();
				File newFile = new File(destFolder, fileName);

				System.out.println("file unzip : " + newFile.getAbsoluteFile());

				//create all non exists folders
				//else you will hit FileNotFoundException for compressed folder
				new File(newFile.getParent()).mkdirs();

				FileOutputStream fos = new FileOutputStream(newFile);

				try {
					int n = 0;

					while ((n = zis.read(buffer, 0, BUFFER_SIZE)) != -1) {
						fos.write(buffer, 0, n);
					}
					fos.flush();
					fos.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				// set permissions to run
				Shell.SU.run("chmod 755 " + newFile.getAbsolutePath());

				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

			System.out.println("Done");

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static boolean isZip(File zipFile) {
		return zipFile.getName().endsWith(".zip");
	}


	public static int copy(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];

		BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
		BufferedOutputStream out = new BufferedOutputStream(output, BUFFER_SIZE);
		int count = 0, n = 0;
		try {
			while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
				out.write(buffer, 0, n);
				count += n;
			}
			out.flush();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				Log.e(e.getMessage(), e.toString());
			}
			try {
				in.close();
			} catch (IOException e) {
				Log.e(e.getMessage(), e.toString());
			}
		}
		return count;
	}

}
