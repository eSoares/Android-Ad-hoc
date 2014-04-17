package pt.it.esoares.android.UI;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import pt.it.esoares.android.olsr.TestOLSRExistence;
import pt.it.esoares.android.olsrdeployer.R;
import pt.it.esoares.android.util.CopyFromRawArg;
import pt.it.esoares.android.util.FileCopy;
import pt.it.esoares.android.util.FileRemover;
import pt.it.esoares.android.wpa_supplicant.GenerateWPA_supplicant;
import pt.it.esoares.android.wpa_supplicant.TestWpaCliExistence;
import pt.it.esoares.android.wpa_supplicant.WpaCliDeployException;
import pt.it.esoares.android.wpa_supplicant.WpaCliDeployListener;
import pt.it.esoares.android.wpa_supplicant.WpaCliDeployer;

import java.io.File;

public class OLSR_Deployer extends ActionBarActivity {
	Boolean existsOLSR;
	static TextView statusOfOLSR;
	ProgressDialog dialog;
	private String OLSRD_PATH;
	private String WPACLI_PATH;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_olsr__deployer);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
		dialog = new ProgressDialog(this);
		OLSRD_PATH = getFilesDir().getAbsolutePath() + File.separatorChar + "olsrd";
		WPACLI_PATH = getFilesDir().getAbsolutePath() + File.separatorChar + "wpa_cli";
		Log.d("OLSR DEPLOYER", "Generated path: "+OLSRD_PATH);
		// findViewById(R.id.txt_status_olsr);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.olsr__deployer, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_olsr__deployer, container, false);
			statusOfOLSR = (TextView) rootView.findViewById(R.id.txt_status_olsr);
			return rootView;
		}
	}

	public void existsOLSR(View v) {
		dialog.setTitle("Loading");
		dialog.show();
		new ExistsOLSR().execute(OLSRD_PATH);
	}

	public void deployOLSR(View v) {
		new PlaceOLSR().execute(new CopyFromRawArg(getResources(), R.raw.olsrd, OLSRD_PATH));
		dialog.show();
	}
	public void removeOLSR(View v) {
		new RemoveOLSR().execute(OLSRD_PATH);
		dialog.show();
	}
	
	public void wpa_supplicant(View v){
		new Write_wpa_supplicant().execute(true);
		dialog.show();
	}
	
	public void existsWPACli(View v){
		new wpa_cli_exists().execute(WPACLI_PATH);
		dialog.show();
	}
	
	public void deployWPACli(View v){
		new WpaCliDeployer(new WpaCliDeployListener() {
			
			@Override
			public void onError(WpaCliDeployException e) {
				dialog.dismiss();
				Toast.makeText(getApplicationContext(), "Failed to deploy wpa_cli",
						Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onDeployStatusUpdate(int percentage, int status) {
			}
			
			@Override
			public void onDeployFinish() {
				dialog.dismiss();
				Toast.makeText(getApplicationContext(), "Sucessfull deployed wpa_cli",
						Toast.LENGTH_SHORT).show();
			}
		}).execute(new CopyFromRawArg(getResources(), R.raw.wpa_cli, WPACLI_PATH));
		dialog.show();
	}
	
	class ExistsOLSR extends TestOLSRExistence {

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				statusOfOLSR.setText(R.string.olsr_found);
				statusOfOLSR.setTextColor(Color.BLACK);
			} else {
				statusOfOLSR.setTextColor(Color.RED);
				statusOfOLSR.setText(R.string.olsr_not_found);
			}
			dialog.dismiss();
		}

	}

	class PlaceOLSR extends FileCopy {

		@Override
		protected void onPostExecute(Boolean result) {
			dialog.dismiss();
			Toast.makeText(getApplicationContext(), result ? "Sucess" : "Failed", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			if (values.length > 0) {
				dialog.setProgress(values[0]);
			}
		}

	}

	class RemoveOLSR extends FileRemover {

		@Override
		protected void onPostExecute(Boolean result) {
			dialog.dismiss();
			Toast.makeText(getApplicationContext(), result ? "Sucess to remove file" : "Failed to remove file",
					Toast.LENGTH_SHORT).show();
		}

	}
	
	class Write_wpa_supplicant extends GenerateWPA_supplicant{

		@Override
		protected void onPostExecute(Boolean result) {
			dialog.dismiss();
			Toast.makeText(getApplicationContext(), result ? "Sucess to update wpa_supplicant" : "Failed to update wpa_supplicant",
					Toast.LENGTH_SHORT).show();
			
		}
		
	}
	
	class wpa_cli_exists extends TestWpaCliExistence{

		@Override
		protected void onPostExecute(Boolean result) {
			dialog.dismiss();
			Toast.makeText(getApplicationContext(), result ? "Found wpa_cli" : "Missing wpa_cli",
					Toast.LENGTH_SHORT).show();
		}
		
	}
	
	

}
