package pt.it.esoares.android.olsr;

public class OLSRSetting {
	private String pluginTXTInfoLocation;
	
	public OLSRSetting() {
		
	}

	public OLSRSetting(String pluginTXTInfoLocation) {
		super();
		this.pluginTXTInfoLocation=pluginTXTInfoLocation;
	}

	public boolean useTXTInfo(){
		return pluginTXTInfoLocation!=null;
	}
	
	public String pluginTXTInfoLocation(){
		return pluginTXTInfoLocation;
	}
}
