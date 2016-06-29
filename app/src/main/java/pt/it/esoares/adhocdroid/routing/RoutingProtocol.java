package pt.it.esoares.adhocdroid.routing;

import android.databinding.ObservableBoolean;
import android.view.View;

import pt.it.esoares.adhocdroid.util.GenericExecutionCallback;
import pt.it.esoares.adhocdroid.util.tasks.RoutingProtocolStartStop;

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

	@SuppressWarnings("unused")// it's being reference in xml via data binding
	public void toggleRun(View view) {
		GenericExecutionCallback callback=new GenericExecutionCallback(){

			@Override
			public void onSuccessfulExecution() {
				isRunning.set(!isRunning.get());
			}

			@Override
			public void onUnsuccessfulExecution() {

			}
		};
		if(this.isRunning.get()){
			RoutingProtocolStartStop.stopRoutingProtocols(callback, this.name);
		}else{
			RoutingProtocolStartStop.startRoutingProtocols(callback, this.name);
		}
	}
}
