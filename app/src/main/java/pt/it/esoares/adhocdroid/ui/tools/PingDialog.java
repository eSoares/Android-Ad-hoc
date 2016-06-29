package pt.it.esoares.adhocdroid.ui.tools;


import pt.it.esoares.adhocdroid.R;

public class PingDialog extends ToolDialog {

	public PingDialog() {
		super();
		setup(R.string.tool_ping, R.string.tool_ping, "ping -c 4 %s");
	}


}
