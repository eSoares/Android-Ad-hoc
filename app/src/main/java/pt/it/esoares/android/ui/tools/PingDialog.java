package pt.it.esoares.android.ui.tools;


import pt.it.esoares.android.olsrdeployer.R;

public class PingDialog extends ToolDialog {

	public PingDialog() {
		super();
		setup(R.string.tool_ping, R.string.tool_ping, "ping -c 4 %s");
	}


}
