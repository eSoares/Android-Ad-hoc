package pt.it.esoares.adhocdroid.devices;

import java.util.List;

import eu.chainfire.libsuperuser.Shell.SU;

public class Supplicant {
	private static final String GET_BASE_SUPPLICANT_FILE="cat ";
	
	/**
	 * Generates a valid supplicant based on the existing one and the {@link Network} that we want to be there.
	 * @param device the current device
	 * @param network the network that we want in there
	 * @return one String that was all the content to write to be a valid supplicant file
	 */
	public static String getSupplicant(Device device, Network network) {
		List<String> content = SU.run(GET_BASE_SUPPLICANT_FILE+device.supplicantFullQualifiedLocation());

		StringBuilder result=new StringBuilder();
		// adds the headers from original supplicant file
		for(String line:content){
			if(line.startsWith("network")){
				break; // end of headers of file, now is describing networks
			} 
			if(line.contains("ap_scan")){
				continue; //doesn't add ap_scan, that will be added later
			}
			if(line.contains("update_config")){
				continue;
			}
		}
		
		result.append("ap_scan=2\n");
		result.append("update_config=1\n");
		//result.append("eapol_version=1\n"); why?
		result.append("\n");
		result.append("network={\n");
		result.append(network.toSupplicant());
		result.append("}\n");
		result.append("\n");
		return result.toString();
	}
	
}
