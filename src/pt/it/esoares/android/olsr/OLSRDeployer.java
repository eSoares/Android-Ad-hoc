package pt.it.esoares.android.olsr;

import android.os.AsyncTask;

public abstract class OLSRDeployer extends AsyncTask<String, Integer, Boolean> {

	@Override
	protected Boolean doInBackground(String... params) {
		if(params.length<1){
			return false;
		}
		String filesPath=params[0];
		
		
		
		return null;
	}
	
	@Override
	abstract protected void onPostExecute(Boolean result);
	
	@Override
	abstract protected void onProgressUpdate(Integer... values);
}
