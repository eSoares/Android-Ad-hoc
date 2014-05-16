package pt.it.esoares.android.wpa_supplicant;

public interface WpaCliDeployListener {

	void onError(WpaCliDeployException e);
	
	void onDeployStatusUpdate(int percentage, int status);
	
	void onDeployFinish();
}
