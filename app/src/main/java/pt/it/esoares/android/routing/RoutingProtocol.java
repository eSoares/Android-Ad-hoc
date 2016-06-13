package pt.it.esoares.android.routing;

import android.databinding.ObservableBoolean;
import android.os.AsyncTask;
import android.view.View;

import java.io.File;

import eu.chainfire.libsuperuser.Shell;

/**
 * A dummy item representing a piece of name.
 */
public class RoutingProtocol {
	public final String id;
	public final String name;
	public final boolean hasSettings;
	public ObservableBoolean isRunning = new ObservableBoolean(false);

	public RoutingProtocol(String id, String name, boolean hasSettings) {
		this.id = id;
		this.name = name;
		this.hasSettings = hasSettings;
	}

	@Override
	public String toString() {
		return "RoutingProtocol{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", hasSettings=" + hasSettings +
				", isRunning=" + isRunning +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		RoutingProtocol that = (RoutingProtocol) o;

		if (hasSettings != that.hasSettings) {
			return false;
		}
		if (id != null ? !id.equals(that.id) : that.id != null) {
			return false;
		}
		return name != null ? name.equals(that.name) : that.name == null;

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (hasSettings ? 1 : 0);
		return result;
	}

	public void toggleRun(View view) {
		new AsyncTask<RoutingProtocol, Void, Void>() {

			@Override
			protected Void doInBackground(RoutingProtocol... params) {
				if (params == null || params.length < 1) {
					return null;
				}
				String location = RoutingProtocolsContent.PROTOCOLS_LOCATION + File.separator + params[0].name;
				if(!params[0].isRunning.get()) {
					Shell.SU.run(location + File.separator + "start.sh");//todo docs
				}else {
					Shell.SU.run(location + File.separator + "stop.sh");//todo docs
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				super.onPostExecute(aVoid);
				isRunning.set(!isRunning.get());
			}
		}.execute(this);
	}
}
