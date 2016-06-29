package pt.it.esoares.adhocdroid.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import pt.it.esoares.adhocdroid.ui.GpsSettingsDialog;

public class GpsLocationService extends Service {
	public static final String EXTRA_START = "start";
	private static final String TAG = "GpsLocationService";
	public static boolean isRunning = false;

	private BlockingQueue<Location> locations = new LinkedBlockingQueue<>(20);
	private Thread locationLogger;
	private LocationListener locationListener;

	public GpsLocationService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Nop");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent.hasExtra(EXTRA_START)) {
			startLocationReceiving();
			isRunning = true;
		} else {
			if (locationLogger != null) {
				locationLogger.interrupt();
			}
			this.stopSelf();
			isRunning = false;
		}
		return Service.START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager != null && locationListener != null) {
			locationManager.removeUpdates(locationListener);
		}
		isRunning = false;
		locationLogger.interrupt();
	}

	private void startLocationReceiving() {
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location provider.
				makeUseOfNewLocation(location);
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};

		// Register the listener with the Location Manager to receive location updates
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String log = prefs.getString(GpsSettingsDialog.GPS_LOG_FILE, GpsSettingsDialog.GPS_DEFAULT_LOG_FILE);
		locationLogger = new Thread(new LocationLogger(new File(Environment.getExternalStorageDirectory(), log)));
		locationLogger.start();
	}

	@SuppressLint("DefaultLocale")
	private void makeUseOfNewLocation(Location location) {
		locations.offer(location);
	}


	class LocationLogger implements Runnable {

		private final File file;

		LocationLogger(File file) {

			this.file = file;
		}

		@SuppressLint("DefaultLocale")
		@Override
		public void run() {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			if(!file.exists()){
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			FileWriter writer = null;
			try {
				writer = new FileWriter(file);
				Location location;
				while (true) {
					location = locations.take();
					writer.write(String.format("%s lat: %.2f long: %.2f at %.2fm\n", sdf.format(new Date(location.getTime())), location.getLatitude(), location.getLongitude(), location.getAltitude()));
					writer.flush();
				}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
