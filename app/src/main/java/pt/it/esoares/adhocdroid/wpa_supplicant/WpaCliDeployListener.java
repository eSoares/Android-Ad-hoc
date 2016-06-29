package pt.it.esoares.adhocdroid.wpa_supplicant;

public interface WpaCliDeployListener {

	void onError(WpaCliDeployException e);
	
	void onDeployStatusUpdate(int percentage, int status);
	
	void onDeployFinish();
}
