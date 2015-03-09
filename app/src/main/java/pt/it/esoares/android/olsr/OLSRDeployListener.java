package pt.it.esoares.android.olsr;

public interface OLSRDeployListener {

	void onError(OLSRDeployException e);
	
	void onDeployStatusUpdate(int percentage, int status);
	
	void onDeployFinish();
}
