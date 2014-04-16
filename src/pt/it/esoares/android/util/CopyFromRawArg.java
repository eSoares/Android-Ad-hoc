package pt.it.esoares.android.util;

import android.content.res.Resources;

public class CopyFromRawArg {
	private Resources resource;
	private int ID;
	private String destination;

	public Resources getResource() {
		return resource;
	}

	public int getID() {
		return ID;
	}

	public String getDestination() {
		return destination;
	}

	public CopyFromRawArg(Resources resource, int iD, String destination) {
		super();
		this.resource = resource;
		ID = iD;
		this.destination = destination;
	}

}